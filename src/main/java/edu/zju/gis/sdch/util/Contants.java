package edu.zju.gis.sdch.util;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/27
 */
public interface Contants {
    String ADDRESS = "address";
    String ADMIN_CODE = "district";
    String ADMIN_NAME = "district_text";
    String POI_TYPE = "class_id";
    String POI_TYPE_NAME = "class_text";
    String ENTITY_TYPE = "clas_id";
    String ENTITY_TYPE_NAME = "clas_text";
    String LON = "lon";
    String LNG = "lng";
    String LAT = "lat";
    String DISTANCE = "distance";
    String GEOMETRY = "geometry";
    String PRIORITY = "priority";
    String ES_POINT = "the_point";
    String ES_SHAPE = "the_shape";

    interface Category {
        String FRAMEWORK = "sdmap";
        String TOPIC = "topic";
        String ENTITY = "entity";
        String POI = "poi";
    }
    interface IndexType {
        String FRAMEWORK = "framework";
        String TOPIC = "topic";
    }

    interface IndexDataType {
        String ENTITY = "entity";
        String POI = "poi";
    }

}
