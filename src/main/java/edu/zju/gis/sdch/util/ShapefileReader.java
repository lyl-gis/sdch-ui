package edu.zju.gis.sdch.util;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

import java.util.Map;

/**
 * 容易出现中文乱码
 *
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/15
 */
public class ShapefileReader extends AbstractReader {
    private DataSource dataSource;
    private Layer layer;
    private SpatialReference sr;

    public ShapefileReader(String shpPath, String charset) {
        if (charset != null && !charset.isEmpty())
            gdal.SetConfigOption("SHAPE_ENCODING", charset);
        dataSource = ogr.Open(shpPath);
        layer = dataSource.GetLayer(0);
        sr = layer.GetSpatialRef();
    }

    public SpatialReference getSpatialReference() {
        return sr;
    }

    public Map<String, Integer> getFieldTypes() {
        return GdalHelper.getFieldTypes(layer);
    }

    public Feature nextFeature() {
        return layer.GetNextFeature();
    }

}
