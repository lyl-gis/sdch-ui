package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.Index;

public interface IndexMapper {
    int deleteByPrimaryKey(String indice);

    int insert(Index record);

    int insertSelective(Index record);

    Index selectByPrimaryKey(String indice);

    int updateByPrimaryKeySelective(Index record);

    int updateByPrimaryKey(Index record);
}