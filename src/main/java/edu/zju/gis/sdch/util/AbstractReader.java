package edu.zju.gis.sdch.util;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Layer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/21
 */
public abstract class AbstractReader implements Closeable {
    DataSource dataSource;
    String[] layerNames;
    int[] layerTypes;

    public String[] getLayerNames() {
        return layerNames;
    }

    public int[] getLayerTypes() {
        return layerTypes;
    }

    public Layer getLayer(String layerName) {
        return dataSource.GetLayer(layerName);
    }

    public long getFeatureCount(String layerName) {
        Layer layer = dataSource.GetLayer(layerName);
        return layer == null ? 0 : layer.GetFeatureCount();
    }

    public long getFeatureCount(Layer layer) {
        return layer.GetFeatureCount();
    }

    public Map<String, Integer> getFields(String layerName) {
        Layer layer = dataSource.GetLayer(layerName);
        return GdalHelper.getFieldTypes(layer);
    }

    public Map<String, String> getFieldTypeNames(String layerName) {
        Layer layer = dataSource.GetLayer(layerName);
        return GdalHelper.getFieldTypeNames(layer);
    }

    @Override
    public void close() throws IOException {
        dataSource.delete();
    }
}
