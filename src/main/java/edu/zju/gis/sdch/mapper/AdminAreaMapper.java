package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.AdminArea;

public interface AdminAreaMapper {
    int deleteByPrimaryKey(String code);

    int insert(AdminArea record);

    int insertSelective(AdminArea record);

    AdminArea selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(AdminArea record);

    int updateByPrimaryKeyWithBLOBs(AdminArea record);

    int updateByPrimaryKey(AdminArea record);
}