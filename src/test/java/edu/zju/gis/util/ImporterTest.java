package edu.zju.gis.util;

import edu.zju.gis.tool.Importer;
import org.junit.Test;

import java.io.IOException;

public class ImporterTest {
    @Test
    public void testInsertXzm() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\xzm.properties", "E:\\data\\shiti\\BOUA6乡镇面.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertDaolu() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\daolu.properties", "E:\\data\\实体查询\\daolu.shp"
                , "GBK", ""});
    }

    @Test
    public void testInsertPOI() throws IOException {
        Importer.main(new String[]{"C:\\Users\\SDGT\\Desktop\\sdch-ui\\src\\main\\resources\\conf\\poi.properties", "E:\\data\\实体查询\\poi.shp"
                , "UTF-8", ""});
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
}
