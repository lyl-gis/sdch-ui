package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.PoiType;

import java.util.List;

public interface PoiTypeMapper {
    int deleteByPrimaryKey(String code);

    int insert(PoiType record);

    int insertSelective(PoiType record);

    List<PoiType> selectAll();
    PoiType selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(PoiType record);

    int updateByPrimaryKey(PoiType record);
}