package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.EntityType;

import java.util.List;

public interface EntityTypeMapper {
    int deleteByPrimaryKey(String code);

    int insert(EntityType record);

    int insertSelective(EntityType record);

    List<EntityType> selectAll();
    EntityType selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(EntityType record);

    int updateByPrimaryKey(EntityType record);
}