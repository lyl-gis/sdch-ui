package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.PoiType;

public interface PoiTypeMapper {
    int deleteByPrimaryKey(String code);

    int insert(PoiType record);

    int insertSelective(PoiType record);

    PoiType selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(PoiType record);

    int updateByPrimaryKey(PoiType record);
}