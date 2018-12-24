package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.EntityType;
import edu.zju.gis.sdch.util.MyBatisUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/20
 */
public class EntityTypeMapperTest {
    private EntityTypeMapper mapper;

    @Before
    public void setup() {
        mapper = MyBatisUtil.getMapper(EntityTypeMapper.class);
    }

    @Test
    public void testInsert() throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("entity_code.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        List<EntityType> entityTypeList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",", -1);
            EntityType entityType = new EntityType();
            String code = fields[0];
            entityType.setCode(fields[0]);
            entityType.setName(fields[1]);
            if (code.endsWith("00000"))
                code = "";
            else if (code.endsWith("0000"))
                code = code.substring(0, 1) + "00000";
            else if (code.endsWith("00"))
                code = code.substring(0, 2) + "0000";
            else
                code = code.substring(0, 4) + "00";
            entityType.setPCode(code);
//            mapper.insert(entityType);
            entityTypeList.add(entityType);
        }
        entityTypeList.forEach(m -> mapper.insert(m));
    }
}
