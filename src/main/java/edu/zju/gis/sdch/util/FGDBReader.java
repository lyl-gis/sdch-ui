package edu.zju.gis.sdch.util;

import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

import java.util.Objects;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/20
 */
public class FGDBReader extends AbstractReader {

    public FGDBReader(String gdb) {
        dataSource = Objects.requireNonNull(ogr.Open(gdb));
        int layerCount = dataSource.GetLayerCount();
        layerNames = new String[layerCount];
        layerTypes = new int[layerCount];
        for (int i = 0; i < layerCount; i++) {
            Layer layer = dataSource.GetLayer(i);
            layerNames[i] = layer.GetName();
            layerTypes[i] = layer.GetLayerDefn().GetGeomType();
        }
    }
}
