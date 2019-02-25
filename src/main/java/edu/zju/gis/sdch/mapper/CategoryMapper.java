package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(String id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectByPId(String pId);

    List<Category> selectByFunc(String func);
}