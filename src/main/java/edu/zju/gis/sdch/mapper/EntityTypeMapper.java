package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.EntityType;

public interface EntityTypeMapper {
    int deleteByPrimaryKey(String code);

    int insert(EntityType record);

    int insertSelective(EntityType record);

    EntityType selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(EntityType record);

    int updateByPrimaryKey(EntityType record);
}