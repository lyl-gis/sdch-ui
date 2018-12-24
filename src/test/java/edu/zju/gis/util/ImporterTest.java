package edu.zju.gis.util;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import edu.zju.gis.sdch.util.FGDBReader;
import edu.zju.gis.sdch.util.GdalHelper;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImporterTest {
    private CommonSetting setting;
    private FGDBReader reader;
    private ElasticSearchHelper helper;


    @Before
    public void setup() throws IOException {
        ogr.RegisterAll();
        String gdb = "F:\\Project\\山东国土测绘院\\data.gdb";
        InputStream is = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("config.properties"));
        // 创建会话工厂，传入mybatis的配置文件信息
        Properties props = new Properties();
        props.load(is);
        is.close();
        setting = new CommonSetting();
        setting.setEsName(props.getProperty("es.name", "elasticsearch"));
        setting.setEsHosts(Arrays.asList(props.getProperty("es.hosts").split(",")));
        setting.setEsPort(Integer.parseInt(props.getProperty("es.port", "9300")));
        setting.setEsShards(Integer.parseInt(props.getProperty("es.number_of_shards", "4")));
        setting.setEsReplicas(Integer.parseInt(props.getProperty("es.number_of_replicas", "0")));
        setting.setEsFieldBoostDefault(Float.parseFloat(props.getProperty("es.field_boost_default", "4.0f")));
        helper = new ElasticSearchHelper(setting.getEsHosts(), setting.getEsPort(), setting.getEsName());
        reader = new FGDBReader(gdb);
    }

    @Test
    public void testInsertXzm() throws IOException {
        Layer layer = reader.getLayer("BOUA6乡镇面");
        Map<String, Integer> fields = GdalHelper.getFieldTypes(layer);
        fields.remove("PAC");
        fields.remove("AdCode");
        fields.remove("地市代");
        fields.remove("地市");
        fields.remove("县区代");
        fields.remove("县区");
        fields.remove("newname");
        fields.remove("Shape_Length");
        fields.remove("Shape_Area");
        String uuidField = "ENTIID6";
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("NAME", "name");
        fieldMapping.put("乡镇代", "district");
        fieldMapping.put("乡镇", "district_text");
        fieldMapping.put("ENTIID6", "lsid");
        Map<String, Float> analyzable = new HashMap<>();
        analyzable.put("name", 4.0f);
        Importer importer = new Importer(helper, setting, "sdmap", "xzm", layer, fields, uuidField, fieldMapping
                , analyzable, true);
        importer.exec();
    }

    @Test
    public void testInsertDaolu() throws IOException {
        Layer layer = reader.getLayer("daolu");
        Map<String, Integer> fields = GdalHelper.getFieldTypes(layer);
        fields.remove("ELEMID");
        fields.remove("ENTIID");
        fields.remove("ENTIID1");
        fields.remove("ENTIID2");
        fields.remove("ENTIID3");
        fields.remove("ENTIID4");
        fields.remove("ENTIID5");
        fields.remove("CLASID92");
        fields.remove("RoadNo");
        fields.remove("CITY");
        fields.remove("SHAPE_Leng");
        fields.remove("Shape_Length");
        fields.remove("Shape_Area");
        String uuidField = "ENTIID6";
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("NAME", "name");
        fieldMapping.put("NAME1", "name1");
        fieldMapping.put("NAME2", "name2");
        fieldMapping.put("NAME3", "name3");
        fieldMapping.put("NAME4", "name4");
        fieldMapping.put("NAME5", "name5");
        fieldMapping.put("NAME6", "name6");
        fieldMapping.put("NAME7", "name7");
        fieldMapping.put("TYPE", "type");
        fieldMapping.put("AdCode", "district");
        fieldMapping.put("乡镇", "district_text");
        fieldMapping.put("CLASID", "clasid");
        fieldMapping.put("ENTIID6", "lsid");
        Map<String, Float> analyzable = new HashMap<>();
        analyzable.put("name", 4.0f);
        analyzable.put("name1", 4.0f);
        analyzable.put("name2", 4.0f);
        analyzable.put("name3", 4.0f);
        analyzable.put("name4", 4.0f);
        analyzable.put("name5", 4.0f);
        analyzable.put("name6", 4.0f);
        analyzable.put("name7", 4.0f);
        Importer importer = new Importer(helper, setting, "fe_road", "road", layer, fields, uuidField, fieldMapping
                , analyzable, true);
        importer.exec();
    }

    @Test
    public void testInsertPOI() throws IOException {
        Layer layer = reader.getLayer("poi");
        Map<String, Integer> fields = GdalHelper.getFieldTypes(layer);
        fields.remove("OBJECTID");
        fields.remove("LX");
        fields.remove("TC");
        fields.remove("REALLEVEL");
        fields.remove("SEARCH");
        fields.remove("REV_GEO");
        fields.remove("VERSION");
        fields.remove("LANDMARK");
        fields.remove("地标");
        fields.remove("SOURCE");
        fields.remove("MEMO");
        fields.remove("OPTIME");
        fields.remove("OPTYPE");
        fields.remove("USERNAME");
        fields.remove("STARTTIME");
        fields.remove("ENDTIME");
        fields.remove("Shape_Length");
        fields.remove("Shape_Area");
        String uuidField = "POI_ID";
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("NAME", "name");
        fieldMapping.put("ABBREVIATION", "abbreviation");
        fieldMapping.put("ADDRESS", "address");
        fieldMapping.put("TELEPHONE", "telephone");
        fieldMapping.put("PRIORITY", "priority");
        fieldMapping.put("KIND", "kind");
        fieldMapping.put("ADMINCODE", "district");
        fieldMapping.put("ADMINNAME", "district_text");
        fieldMapping.put("POI_ID", "lsid");
        fieldMapping.put("ADDCODE", "addcode");
        Map<String, Float> analyzable = new HashMap<>();
        analyzable.put("name", 4.0f);
        Importer importer = new Importer(helper, setting, "f_poi", "poi", layer, fields, uuidField, fieldMapping
                , analyzable, true);
        importer.exec();
    }

    @Test
    public void testInsertShuixi() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\shuixi.properties", "E:\\data\\shiti\\shuixi.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertZhengqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\zq.properties", "E:\\data\\shiti\\zhengqu.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertBaoshuiqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\bsq.properties", "E:\\data\\实体查询\\保税区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertDizhigongyuan() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\dzgy.properties", "E:\\data\\实体查询\\地质公园.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertFengjingmingshengqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\fjmsq.properties", "E:\\data\\shiti\\风景名胜区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertGuojiajigaoxinqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\gjjgxq.properties", "E:\\data\\实体查询\\国家级高新区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertGuojiajikaifaqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\gjjkfq.properties", "E:\\data\\实体查询\\国家级开发区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertGuojiasenlingongyuan() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\gjjslgy.properties", "E:\\data\\shiti\\国家级森林公园.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertGuojialvyoudujiaqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\gjlydjq.properties", "E:\\data\\实体查询\\国家旅游度假区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertShengjigaoxinqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\sjgxq.properties", "E:\\data\\实体查询\\省级高新区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertShengjikaifaqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\sjkfq.properties", "E:\\data\\shiti\\省级开发区.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertShengjisenlingongyuan() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\sjslgy.properties", "E:\\data\\实体查询\\省级森林公园.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertShijieziranwenhuayichan() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\sjzrwhyc.properties", "E:\\data\\实体查询\\世界自然文化遗产.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertWenhuayichan() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\whyc.properties", "E:\\data\\实体查询\\文化遗产.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertZiranbaohuqu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\zrbhq.properties", "E:\\data\\实体查询\\自然保护区.shp"
                , "GBK", ""});
    }

    @Test
    public void testIn() {
        System.out.println(Integer.MAX_VALUE);
    }

    @Test
    public void putMapping() {
        JSONObject source = new JSONObject();
        JSONObject properties = new JSONObject();
        source.put("properties", properties);
        properties.put("dtype", new JSONObject().put("type", "keyword").put("store", true));
        helper.putMapping("sdmap", "_doc", source.toString());
    }

    @Test
    public void clearIndex() {
        helper.delete("fe_road", "_doc");
    }

    @Test
    public void testCount() {
        System.out.println(helper.getDocCount("f_poi", "_doc"));
    }
}