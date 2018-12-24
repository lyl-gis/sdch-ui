package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.Category;
import edu.zju.gis.sdch.util.MyBatisUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/21
 */
public class CategoryMapperTest {
    private CategoryMapper mapper;

    @Before
    public void setup() {
        mapper = MyBatisUtil.getMapper(CategoryMapper.class);
    }

    @Test
    public void testInsertTopCategory() {
        Category category = new Category();
        category.setPId("");
        category.setFunc("sdmap");
        category.setDescription("框架数据，包括实体数据和地名地址数据（POI）");
        mapper.insert(category);
        System.out.println(category);
    }

    @Test
    public void testInsertSubCategory() {
        Category parent = mapper.selectByPrimaryKey("9297813f-051e-11e9-833e-3497f6d6d1a5");
        Category category = new Category();
        category.setPId(parent.getId());
        category.setFunc("entity");
        category.setDescription("实体数据");
        mapper.insert(category);
        category = new Category();
        category.setPId(parent.getId());
        category.setFunc("poi");
        category.setDescription("地名地址数据（主要为POI数据）");
        mapper.insert(category);
        System.out.println(category);
    }
}
