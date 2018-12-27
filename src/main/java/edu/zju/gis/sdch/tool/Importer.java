package edu.zju.gis.sdch.tool;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexMappingMapper;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.model.IndexMapping;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import edu.zju.gis.sdch.util.GdalHelper;
import edu.zju.gis.sdch.util.MyBatisUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/15
 */
public class Importer {
    private static final Logger log = LogManager.getLogger(Importer.class);
    private ElasticSearchHelper helper;
    /**
     * 索引名
     */
    private String indice;
    /**
     * 数据类型（如poi数据、水系数据）
     */
    private String dtype;
    private Layer layer;
    /**
     * name-ogr数据类型
     */
    private Map<String, Integer> fields;
    /**
     * 可视作唯一标识的字段名srcName
     */
    private String uuidField;
    /**
     * srcName-destName
     */
    private Map<String, String> fieldMapping;
    /**
     * 可用于模糊搜索的字段，destName-匹配程度因子（默认为1.0，表示在相似度打分上乘以此因子作为最终打分）
     */
    private Map<String, Float> analyzable;
    /**
     * 是否跳过空间范围为空的记录
     */
    private boolean skipEmptyGeom;
    private CommonSetting setting;
    @Getter
    @Setter
    private Integer shards;
    @Getter
    @Setter
    private Integer replicas;
    @Getter
    @Setter
    private String description;
    private IndexMapper indexMapper;
    private IndexMappingMapper indexMappingMapper;

    public Importer(ElasticSearchHelper helper, CommonSetting setting, String indice, String dtype, Layer layer, Map<String, Integer> fields
            , String uuidField, Map<String, String> fieldMapping, Map<String, Float> analyzable, boolean skipEmptyGeom) {
        this.helper = helper;
        this.indice = indice;
        this.dtype = dtype;
        this.layer = layer;
        this.fields = fields;
        this.uuidField = uuidField;
        this.fieldMapping = fieldMapping;
        this.analyzable = analyzable;
        this.skipEmptyGeom = skipEmptyGeom;
        this.setting = setting;
        indexMapper = MyBatisUtil.getMapper(IndexMapper.class);
        indexMappingMapper = MyBatisUtil.getMapper(IndexMappingMapper.class);
    }

    public void exec() throws IOException {
        Index index = indexMapper.selectByPrimaryKey(indice);
        if (index == null) {
            if (shards == null)
                shards = setting.getEsShards();
            if (replicas == null)
                replicas = setting.getEsReplicas();
            //创建索引
            if (helper.createIfNotExist(indice, shards, replicas)) {
                index = new Index();
                index.setIndice(indice);
                index.setDtype(dtype);
                index.setShards(shards);
                index.setReplicas(replicas);
                index.setGeoType(GdalHelper.getGeoTypeName(layer.GetGeomType()));
                index.setDescription(description == null ? "" : description);
                index.setCreateTime(new Date());
                index.setSize(0L);
                indexMapper.insert(index);//更新索引创建信息到数据库
            } else {
                throw new IOException("索引`" + indice + "`创建失败");
            }
        }
        List<IndexMapping> mappings = indexMappingMapper.selectByIndice(indice);//获取索引字段信息
        List<String> mappingFields = mappings.stream().map(IndexMapping::getFieldName).collect(Collectors.toList());
        Map<String, Integer> maps = new HashMap<>();
        List<IndexMapping> newMappings = new ArrayList<>();
        Date now = new Date();
        for (String field : fields.keySet()) {
            String destName = fieldMapping.getOrDefault(field, field);
            if (!mappingFields.contains(destName)) {//当有新增字段时，同步更新到数据库
                maps.put(destName, fields.get(field));
                IndexMapping mapping = new IndexMapping();
                mapping.setIndice(indice);
                mapping.setFieldName(destName);
                mapping.setFieldType(GdalHelper.getTypeName(fields.get(field)));
                mapping.setBoost(analyzable.getOrDefault(destName, 1.0f));
                mapping.setAnalyzable(analyzable.containsKey(destName));
                mapping.setCreateTime(now);
                newMappings.add(mapping);
            }
        }
        if (!maps.isEmpty()) {
            JSONObject mappingSource = addMappingFields(maps, analyzable, setting);
            if (!helper.exists(indice, "_doc"))
                mappingSource.put("dynamic", false).put("_all", new JSONObject().put("enabled", false));
            if (helper.putMapping(indice, "_doc", mappingSource.toString())) {
                indexMappingMapper.insertByBatch(newMappings);//更新mapping到数据库
            }
        }
        layer.ResetReading();//重置图层要素读取的游标位置

        SpatialReference sr = layer.GetSpatialRef();
        CoordinateTransformation transformation = null;
        if (sr.IsProjected() == 1)
            transformation = osr.CreateCoordinateTransformation(sr, sr.CloneGeogCS());
        Map<String, Map<String, Object>> records = GdalHelper.getNextNFeatures(layer, 1000, fields, uuidField, skipEmptyGeom, transformation);
//      //自己加了一行代码
//       DataPreview.myrecords=records;
//        //得到预览的前1000条数据在records中
        Set<String> destKeys = fieldMapping.keySet();
        long error = 0;
        long count = 0;
        while (!records.isEmpty()) {
            records.values().forEach(m -> {
                String[] keys = m.keySet().toArray(new String[0]);
                m.put("dtype", dtype);
                for (String key : keys)
                    if (destKeys.contains(key))
                        m.put(fieldMapping.get(key), m.remove(key));
            });
            error += helper.upsert(indice, "_doc", records);
            count += records.size();
            log.info("当前已入库失败{}条记录", error);
            records = GdalHelper.getNextNFeatures(layer, 1000, fields, uuidField, skipEmptyGeom, transformation);
        }
        Long size = helper.getDocCount(indice, "_doc");
        index.setSize(size);
        indexMapper.updateByPrimaryKeySelective(index);
    }

    public static void main(String[] args) throws IOException {

    }

    private static JSONObject addMappingFields(Map<String, Integer> fields, Map<String, Float> analyzable, CommonSetting setting) {
        JSONObject source = new JSONObject();
        JSONObject properties = new JSONObject();
        source.put("properties", properties);
        for (String field : fields.keySet()) {
            switch (fields.get(field)) {
                case ogr.OFTInteger:
                    properties.put(field, new JSONObject().put("type", "integer").put("store", true));
                    break;
                case ogr.OFTInteger64:
                    properties.put(field, new JSONObject().put("type", "long").put("store", true));
                    break;
                case ogr.OFTReal:
                    properties.put(field, new JSONObject().put("type", "float").put("store", true));
                    break;
                case ogr.OFTString:
                    if (analyzable.containsKey(field))
                        properties.put(field, new JSONObject().put("type", "text").put("store", true)
                                .put("analyzer", "ik_max_word").put("search_analyzer", "ik_smart")
                                .put("boost", analyzable.getOrDefault(field, setting.getEsFieldBoostDefault())));
                    else
                        properties.put(field, new JSONObject().put("type", "text").put("store", true));
                    break;
            }
        }
        properties.put("dtype", new JSONObject().put("type", "keyword").put("store", true));
        properties.put("the_shape", new JSONObject().put("type", "geo_shape"));
        properties.put("the_point", new JSONObject().put("type", "geo_point"));
        return source;
    }
}
