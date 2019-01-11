package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.IndexMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexMappingMapper {
    int deleteByPrimaryKey(String id);

    int insert(IndexMapping record);

    int insertSelective(IndexMapping record);

    IndexMapping selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(IndexMapping record);

    int updateByPrimaryKey(IndexMapping record);

    List<IndexMapping> selectByIndice(String indice);

    IndexMapping selectByField(@Param("indice") String indice, @Param("field") String field);

    void insertByBatch(List<IndexMapping> mappings);

    int deleteByIndice(String indice);

    List<IndexMapping> selectByIndices(List<String> indices);
}