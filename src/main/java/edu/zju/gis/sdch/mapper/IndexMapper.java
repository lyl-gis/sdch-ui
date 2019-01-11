package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.Index;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexMapper {
    int deleteByPrimaryKey(String indice);

    int insert(Index record);

    int insertSelective(Index record);

    Index selectByPrimaryKey(String indice);
    List<Index> selectAll();
    int updateByPrimaryKeySelective(Index record);

    int updateByPrimaryKey(Index record);

    int getCount();

    List<Index> selectByPage(@Param("offset") int offset, @Param("size") int size);

}