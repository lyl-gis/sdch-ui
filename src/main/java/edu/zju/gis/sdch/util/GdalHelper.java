package edu.zju.gis.sdch.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;
import org.gdal.osr.CoordinateTransformation;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/20
 */
public class GdalHelper {
    private static final Logger log = LogManager.getLogger(GdalHelper.class);
    private static final Map<Integer, String> fieldTypes = new HashMap<>();
    private static final Map<Integer, String> geomTypes = new HashMap<>();

    static {
        ogr.RegisterAll();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        fieldTypes.put(ogr.OFTInteger, "integer");
        fieldTypes.put(ogr.OFTInteger64, "long");
        fieldTypes.put(ogr.OFTReal, "float");
        fieldTypes.put(ogr.OFTString, "string");
        geomTypes.put(ogr.wkbPoint, "POINT");
        geomTypes.put(ogr.wkbPoint25D, "POINT");
        geomTypes.put(ogr.wkbPointM, "POINT");
        geomTypes.put(ogr.wkbPointZM, "POINT");
        geomTypes.put(ogr.wkbLineString, "LINESTRING");
        geomTypes.put(ogr.wkbPolygon, "POLYGON");
        geomTypes.put(ogr.wkbMultiPoint, "MULTIPOINT");
        geomTypes.put(ogr.wkbMultiPoint25D, "POINT");
        geomTypes.put(ogr.wkbMultiPointM, "POINT");
        geomTypes.put(ogr.wkbMultiPointZM, "POINT");
        geomTypes.put(ogr.wkbMultiLineString, "MULTILINESTRING");
        geomTypes.put(ogr.wkbMultiPolygon, "MULTIPOLYGON");
        geomTypes.put(ogr.wkbGeometryCollection, "GEOMETRYCOLLECTION");
    }

    public static String getTypeName(int ogrFieldType) {
        return fieldTypes.get(ogrFieldType);
    }

    public static String getGeoTypeName(int ogrGeoType) {
        return geomTypes.get(ogrGeoType);
    }

    public static Map<String, String> getFieldTypeNames(Layer layer) {
        FeatureDefn featureDefn = layer.GetLayerDefn();
        int fieldCount = featureDefn.GetFieldCount();
        Map<String, String> fieldTypes = new HashMap<>();
        for (int i = 0; i < fieldCount; i++) {
            FieldDefn fieldDefn = featureDefn.GetFieldDefn(i);
            String name = fieldDefn.GetName();
            fieldTypes.put(name, fieldDefn.GetTypeName());
        }
        return fieldTypes;
    }

    public static Map<String, Integer> getFieldTypes(Layer layer) {
        FeatureDefn featureDefn = layer.GetLayerDefn();
        int fieldCount = featureDefn.GetFieldCount();
        Map<String, Integer> fieldTypes = new HashMap<>();
        for (int i = 0; i < fieldCount; i++) {
            FieldDefn fieldDefn = featureDefn.GetFieldDefn(i);
            String name = fieldDefn.GetName();
            fieldTypes.put(name, fieldDefn.GetFieldType());
        }
        return fieldTypes;
    }

    public static int getGeometryType(Layer layer) {
        return layer.GetLayerDefn().GetGeomType();
    }

    /**
     * 读取当前游标开始的{@code size}条记录，不包含跳过的空记录
     *
     * @param layer
     * @param size
     * @param fields
     * @param uuidField     记录的唯一标识
     * @param skipEmptyGeom
     * @return
     */
    public static Map<String, Map<String, Object>> getNextNFeatures(Layer layer, long size, Map<String, Integer> fields
            , String uuidField, boolean skipEmptyGeom, CoordinateTransformation transformation) {
        Map<String, Map<String, Object>> records = new HashMap<>();
        Feature feature;
        Set<String> fieldNames = fields.keySet();
        while (records.size() < size && (feature = layer.GetNextFeature()) != null) {//判断顺序不能反
            Geometry geometry = feature.GetGeometryRef();
            if (geometry.IsEmpty() && skipEmptyGeom) {
                log.warn("feature with empty geometry, ID-{}", feature.GetFID());
                continue;
            }
            Map<String, Object> record = new HashMap<>();
            if (!geometry.IsEmpty()) {
                if (geometry.Is3D() != 0)
                    geometry.FlattenTo2D();
                if (transformation != null)
                    geometry.Transform(transformation);
                String geojson = geometry.ExportToJson();
                JSONObject json = new JSONObject(geojson);
                if (json.getString("type").equalsIgnoreCase("point")) {
                    Map<String, Object> geo = new HashMap<>();
                    geo.put("lon", json.getJSONArray("coordinates").get(0));
                    geo.put("lat", json.getJSONArray("coordinates").get(1));
                    record.put("the_point", geo);
                } else {
                    Map<String, Object> geo = new HashMap<>();
                    geo.put("type", json.get("type"));
                    geo.put("coordinates", json.get("coordinates"));
                    record.put("the_shape", geo);
                }
            }
            for (String name : fieldNames) {
                Object value;
                switch (fields.get(name)) {
                    case ogr.OFTInteger:
                        value = feature.GetFieldAsInteger(name);
                        break;
                    case ogr.OFTInteger64:
                        value = feature.GetFieldAsInteger64(name);
                        break;
                    case ogr.OFTReal:
                        value = feature.GetFieldAsDouble(name);
                        break;
                    case ogr.OFTString:
                        value = feature.GetFieldAsString(name);
                        break;
                    default:
                        value = null;
                }
                if (value != null)
                    record.put(name, value);
            }
            if (!record.isEmpty()) {
                String uuid;
                if (uuidField != null && !uuidField.isEmpty() && record.containsKey(uuidField))
                    uuid = record.get(uuidField).toString();
                else
                    uuid = UUID.randomUUID().toString();
                records.put(uuid, record);
            }
        }
        if (records.size() < 500)
            System.out.println(records.size());
        return records;
    }
}
