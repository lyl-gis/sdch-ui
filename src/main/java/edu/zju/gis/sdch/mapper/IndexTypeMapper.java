package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.IndexType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexTypeMapper {
    int insert(IndexType record);

    int insertSelective(IndexType record);

    int deleteByPrimaryKey(String id);

    int updateByPrimaryKeySelective(IndexType record);

    List<IndexType> selectByIndice(String indice);

    IndexType selectByType(@Param("indice") String indice, @Param("dtype") String dtype);
}