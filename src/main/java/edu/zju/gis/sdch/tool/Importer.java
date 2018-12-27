package edu.zju.gis.sdch.tool;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.mapper.CategoryMapper;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexMappingMapper;
import edu.zju.gis.sdch.mapper.IndexTypeMapper;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.model.IndexMapping;
import edu.zju.gis.sdch.model.IndexType;
import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.service.impl.IndexServiceImpl;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import edu.zju.gis.sdch.util.GdalHelper;
import edu.zju.gis.sdch.util.MyBatisUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/15
 */
public class Importer {
    private static final Logger log = LogManager.getLogger(Importer.class);
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
    private String categoryType;
    private String dataType;
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
    private IndexService indexService;

    /**
     * @param categoryType see {@link edu.zju.gis.sdch.util.Contants}
     * @param dataType     see {@link edu.zju.gis.sdch.util.Contants}
     */
    public Importer(ElasticSearchHelper helper, CommonSetting setting, String indice, String dtype, Layer layer
            , Map<String, Integer> fields, String uuidField, Map<String, String> fieldMapping, Map<String, Float> analyzable
            , boolean skipEmptyGeom, String categoryType, String dataType) {
        this.indice = indice;
        this.dtype = dtype;
        this.layer = layer;
        this.fields = fields;
        this.uuidField = uuidField;
        this.fieldMapping = fieldMapping;
        this.analyzable = analyzable;
        this.skipEmptyGeom = skipEmptyGeom;
        this.categoryType = categoryType;
        this.dataType = dataType;
        this.setting = setting;
        this.indexService = new IndexServiceImpl(helper
                , MyBatisUtil.getMapper(CategoryMapper.class)
                , MyBatisUtil.getMapper(IndexMapper.class)
                , MyBatisUtil.getMapper(IndexTypeMapper.class)
                , MyBatisUtil.getMapper(IndexMappingMapper.class));
    }

    public void exec() throws IOException {
        exec(new Observable());
    }

    public void exec(Observable observable) throws IOException {//todo 类别字段确定一下：poi->kind, entity->clasid
        Index index = indexService.getByIndice(indice);
        if (index == null) {
            if (shards == null)
                shards = setting.getEsShards();
            if (replicas == null)
                replicas = setting.getEsReplicas();
            //创建索引
            index = new Index();
            index.setIndice(indice);
            index.setShards(shards);
            index.setReplicas(replicas);
            index.setDescription(description == null ? "" : description);
            index.setCategory(categoryType);
            index.setCreateTime(new Date());
            indexService.createIndex(index);//更新索引创建信息到数据库
        }
        IndexType indexType = new IndexType();
        indexType.setIndice(indice);
        indexType.setDtype(dtype);
        indexType.setDescription(description);
        indexType.setCategory(dataType);
        indexType.setGeoType(GdalHelper.getGeoTypeName(layer.GetGeomType()));
        indexService.upsertIndexType(indexType);
        List<IndexMapping> mappings = indexService.getMappingByIndice(indice);//获取索引字段信息
        List<String> mappingFields = mappings.stream().map(IndexMapping::getFieldName).collect(Collectors.toList());
        List<IndexMapping> newMappings = new ArrayList<>();
        for (String field : fields.keySet()) {
            String destName = fieldMapping.getOrDefault(field, field);
            if (!mappingFields.contains(destName)) {//当有新增字段时，同步更新到数据库
                IndexMapping mapping = new IndexMapping();
                mapping.setIndice(indice);
                mapping.setFieldName(destName);
                mapping.setFieldType(GdalHelper.getTypeName(fields.get(field)));
                mapping.setBoost(analyzable.getOrDefault(destName, 1.0f));
                mapping.setAnalyzable(analyzable.containsKey(destName));
                newMappings.add(mapping);
            }
        }
        indexService.addMapping(newMappings);
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
            error += indexService.upsert(indice, "_doc", records);
            count += records.size();
            observable.notifyObservers(count * 1.0 / layer.GetFeatureCount());
            log.info("当前已入库失败{}条记录", error);
            records = GdalHelper.getNextNFeatures(layer, 1000, fields, uuidField, skipEmptyGeom, transformation);
        }
        observable.notifyObservers(1);
    }
}
