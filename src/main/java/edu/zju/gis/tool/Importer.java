package edu.zju.gis.tool;

import edu.zju.gis.config.CommonSetting;
import edu.zju.gis.util.ElasticSearchHelper;
import edu.zju.gis.util.ShapefileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.*;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/15
 */
public class Importer {
    private static final Logger log = LogManager.getLogger(Importer.class);
    private static List<String> fields = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage:" +
                    "\n\t 0 - config file(.properties)" +
                    "\n\t 1 - shapefile(.shp)/GDB" +
                    "\n\t 2 - 图层名（仅对GDB有效）" +
                    "\n\t 3 - 默认中文编码类型（默认GBK，带有.cpg的数据会使用其原有编码）");
            return;
        }
        fields.addAll(Arrays.asList("id", "lsid", "name", "address", "telephone"
                , "admincode", "zipcode", "kind", "type", "clasid", "addcode", "abbreviation", "priority"));
        for (int i = 1; i < 10; i++) {
            fields.add("name" + i);
        }
        String pConfig = args[0];
        String pShps = args[1];
        String pLayerName = args[2];
        String pCharset = args[3];
        Properties configs = new Properties();
        InputStream is = Files.newInputStream(Paths.get(pConfig));
        configs.load(is);
        CommonSetting setting = CommonSetting.getInstance();
        ElasticSearchHelper helper = ElasticSearchHelper.getInstance();
        String indexName = "sdmap2";//configs.getProperty("index.name", setting.getEsIndex());
        String indexType = configs.getProperty("index.type");
        String fieldUuid = configs.getProperty("fields.uuid");
        int shards = Integer.parseInt(configs.getProperty("es.num_of_shards", "" + setting.getEsShards()));
        int replicas = Integer.parseInt(configs.getProperty("es.num_of_replicas", "" + setting.getEsReplicas()));
        helper.createIfNotExist(indexName, shards, replicas);
        if (!helper.exists(indexName, "_doc")) {
            String mappingSource = getMappingSource();
            helper.putMapping(indexName, "_doc", mappingSource);
        }
        File rootFile = new File(pShps);
        if (pShps.toLowerCase().endsWith(".shp") && rootFile.isFile()) {
            List<Map<String, Object>> records = new ArrayList<>();
            try (ShapefileReader reader = new ShapefileReader(pShps, pCharset)) {
                SpatialReference sr = reader.getSpatialReference();
                CoordinateTransformation transformation = null;
                if (sr.IsProjected() == 1) {
                    SpatialReference geoSr = sr.CloneGeogCS();
                    transformation = osr.CreateCoordinateTransformation(sr, geoSr);
                }
                Feature feature;
                Map<String, Integer> fieldTypes = reader.getFieldTypes();
                int error = 0;
                int count = 0;
                while ((feature = reader.nextFeature()) != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("doc_id", fieldUuid == null ? UUID.randomUUID() : feature.GetFieldAsString(fieldUuid));
                    for (String key : fieldTypes.keySet()) {
                        for (String field : fields) {
                            if (field.equalsIgnoreCase(key)) {
                                Object value = null;
                                switch (fieldTypes.get(key)) {
                                    case ogr.OFTInteger:
                                        value = feature.GetFieldAsInteger(key);
                                        break;
                                    case ogr.OFTString:
                                        value = feature.GetFieldAsString(key);
                                        break;
                                }
                                if (value != null)
                                    map.put(field, value);
                            }
                        }
                        if (key.equalsIgnoreCase("abbreviati"))
                            map.put("abbreviation", feature.GetFieldAsString(key));
                    }
                    map.put("lsid", feature.GetFieldAsString(fieldUuid));
                    Geometry geom = feature.GetGeometryRef();
                    if (geom == null || geom.IsEmpty()) {
                        log.warn("该要素无空间范围：" + map.toString());
                        continue;//todo 跳过空记录
                    }
                    if (geom.Is3D() == 1) {
                        geom.FlattenTo2D();
                    }
                    if (transformation != null)
                        geom.Transform(transformation);
                    String geojson = geom.ExportToJson();
                    JSONObject json = new JSONObject(geojson);
                    Map<String, Object> geo = new HashMap<>();
                    if (json.getString("type").equalsIgnoreCase("point")) {
                        geo.put("lon", json.getJSONArray("coordinates").get(0));
                        geo.put("lat", json.getJSONArray("coordinates").get(1));
                        map.put("the_point", geo);
                    } else {
                        geo.put("type", json.get("type"));
                        geo.put("coordinates", json.get("coordinates"));
                        map.put("the_shape", geo);
                    }
                    map.put("dtype", indexType);
                    records.add(map);
                    if (records.size() >= 1000) {
                        error += helper.publish(indexName, "_doc", records);
                        count += records.size();
                        records.clear();
                        System.out.printf("已成功入库%d条记录\n", count - error);
                    }
                }
                if (records.size() > 0) {
                    error += helper.publish(indexName, "_doc", records);
                    count += records.size();
                    records.clear();
                }
                System.out.printf("成功入库%d条记录，失败%d条记录\n", count - error, error);
            }
        } else if (pShps.toLowerCase().endsWith(".gdb") && rootFile.isDirectory()) {
            DataSource ds = ogr.Open(pShps);
            ds = Objects.requireNonNull(ds);
            Layer layer = ds.GetLayer(pLayerName);
            layer = Objects.requireNonNull(layer);
            SpatialReference sr = layer.GetSpatialRef();
            CoordinateTransformation transformation = null;
            if (sr.IsProjected() == 1) {
                SpatialReference geoSr = sr.CloneGeogCS();
                transformation = osr.CreateCoordinateTransformation(sr, geoSr);
            }
            Feature feature;
            Map<String, Integer> fieldTypes = ShapefileReader.getFieldTypes(layer);
            int error = 0;
            int count = 0;
            List<Map<String, Object>> records = new ArrayList<>();
            while ((feature = layer.GetNextFeature()) != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("doc_id", fieldUuid == null ? UUID.randomUUID() : feature.GetFieldAsString(fieldUuid));
                for (String key : fieldTypes.keySet()) {
                    for (String field : fields) {
                        if (field.equalsIgnoreCase(key)) {
                            Object value = null;
                            switch (fieldTypes.get(key)) {
                                case ogr.OFTInteger:
                                    value = feature.GetFieldAsInteger(key);
                                    break;
                                case ogr.OFTString:
                                    value = feature.GetFieldAsString(key);
                                    break;
                            }
                            if (value != null)
                                map.put(field, value);
                        }
                    }
                }
                map.put("lsid", feature.GetFieldAsString(fieldUuid));
                Geometry geom = feature.GetGeometryRef();
                if (geom == null || geom.IsEmpty()) {
                    log.warn("该要素无空间范围：" + map.toString());
                    continue;//todo 跳过空记录
                }
                if (geom.Is3D() == 1) {
                    geom.FlattenTo2D();
                }
                if (transformation != null)
                    geom.Transform(transformation);
                String geojson = geom.ExportToJson();
                JSONObject json = new JSONObject(geojson);
                Map<String, Object> geo = new HashMap<>();
                if (json.getString("type").equalsIgnoreCase("point")) {
                    geo.put("lon", json.getJSONArray("coordinates").get(0));
                    geo.put("lat", json.getJSONArray("coordinates").get(1));
                    map.put("the_point", geo);
                } else {
                    geo.put("type", json.get("type"));
                    geo.put("coordinates", json.get("coordinates"));
                    map.put("the_shape", geo);
                }
                map.put("dtype", indexType);
                records.add(map);
                if (records.size() >= 1000) {
                    error += helper.publish(indexName, "_doc", records);
                    count += records.size();
                    records.clear();
                    System.out.printf("已成功入库%d条记录\n", count - error);
                }
            }
            if (records.size() > 0) {
                error += helper.publish(indexName, "_doc", records);
                count += records.size();
                records.clear();
            }
            System.out.printf("成功入库%d条记录，失败%d条记录\n", count - error, error);
            layer.delete();
            ds.delete();
        }
    }

    private static String getMappingSource() {
        JSONObject source = new JSONObject();
        source.put("dynamic", false).put("_all", new JSONObject().put("enabled", false));
        JSONObject properties = new JSONObject();
        source.put("properties", properties);
        for (String field : fields) {
            if (field.startsWith("name") || field.equalsIgnoreCase("address"))
                properties.put(field, new JSONObject().put("type", "text").put("store", true).put("analyzer", "ik_max_word"));
            else if (field.equals("priority"))
                properties.put(field, new JSONObject().put("type", "integer").put("store", true));
            else
                properties.put(field, new JSONObject().put("type", "text").put("store", true));
        }
        properties.put("the_point", new JSONObject().put("type", "geo_point"));//cannot set property `store`
        properties.put("the_shape", new JSONObject().put("type", "geo_shape"));
        properties.put("dtype", new JSONObject().put("type", "keyword").put("store", true));
        return source.toString();
    }
}
