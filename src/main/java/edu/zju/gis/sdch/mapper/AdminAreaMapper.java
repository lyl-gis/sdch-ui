package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.AdminArea;

import java.util.List;

public interface AdminAreaMapper {
    int deleteByPrimaryKey(String code);

    int insert(AdminArea record);

    int insertSelective(AdminArea record);

    List<AdminArea> selectAll();
    AdminArea selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(AdminArea record);

    int updateByPrimaryKeyWithBLOBs(AdminArea record);

    int updateByPrimaryKey(AdminArea record);
}