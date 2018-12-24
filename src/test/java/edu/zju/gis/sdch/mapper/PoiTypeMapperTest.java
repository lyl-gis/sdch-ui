package edu.zju.gis.sdch.mapper;

import edu.zju.gis.sdch.model.PoiType;
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
public class PoiTypeMapperTest {
    private PoiTypeMapper mapper;

    @Before
    public void setup() {
        mapper = MyBatisUtil.getMapper(PoiTypeMapper.class);
    }

    @Test
    public void testInsert() throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("poi_code.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        List<PoiType> typeList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",", -1);
            PoiType type = new PoiType();
            String code = fields[0];
            type.setCode(fields[0]);
            type.setName(fields[1]);
            if (code.endsWith("0000"))
                code = "";
            else if (code.endsWith("00"))
                code = code.substring(0, 2) + "0000";
            else
                code = code.substring(0, 4) + "00";
            type.setPCode(code);
//            mapper.insert(entityType);
            typeList.add(type);
        }
        typeList.forEach(m -> mapper.insert(m));
    }

    @Test
    public void testUpdate() throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("KIND_KIND4_SDTYPE.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",", -1);
            PoiType type = new PoiType();
            type.setCode(fields[0]);
            type.setCode4(fields[1]);
            mapper.updateByPrimaryKeySelective(type);
        }
    }
}
