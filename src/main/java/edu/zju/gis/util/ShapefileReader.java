package edu.zju.gis.util;

import edu.zju.gis.config.CommonSetting;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;
import org.gdal.osr.SpatialReference;
import org.json.JSONObject;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 容易出现中文乱码
 *
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/15
 */
public class ShapefileReader implements Closeable {
    private DataSource dataSource;
    private Layer layer;
    private SpatialReference sr;

    public ShapefileReader(String shpPath, String charset) {
        ogr.RegisterAll();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        if (charset != null && !charset.isEmpty())
            gdal.SetConfigOption("SHAPE_ENCODING", charset);
        dataSource = ogr.Open(shpPath);
        layer = dataSource.GetLayer(0);
        sr = layer.GetSpatialRef();
    }

    @Override
    public void close() {
        dataSource.delete();
    }

    public SpatialReference getSpatialReference() {
        return sr;
    }

    public List<String> getFields() {
        return getFields(layer);
    }

    public Map<String, String> getFieldTypeNames() {
        return getFieldTypeNames(layer);
    }

    public Map<String, Integer> getFieldTypes() {
        return getFieldTypes(layer);
    }

    public int getGeometryType() {
        return getGeometryType(layer);
    }

    public void resetReading() {
        layer.ResetReading();
    }

    public Feature nextFeature() {
        return layer.GetNextFeature();
    }

    public static List<String> getFields(Layer layer) {
        FeatureDefn featureDefn = layer.GetLayerDefn();
        int fieldCount = featureDefn.GetFieldCount();
        List<String> fields = new ArrayList<>();
        for (int i = 0; i < fieldCount; i++) {
            FieldDefn fieldDefn = featureDefn.GetFieldDefn(i);
            String name = fieldDefn.GetName();
            fields.add(name);
        }
        return fields;
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

}
