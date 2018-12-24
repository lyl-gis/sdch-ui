package edu.zju.gis.util;

import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.ShapefileReader;
import org.gdal.gdal.gdal;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/18
 */
public class ShapefileReaderTest {
    @Before
    public void setup() {
        ogr.RegisterAll();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
    }

    @Test
    public void testP() {
        System.out.println(ogr.GetDriverCount());
        Driver driver = ogr.GetDriverByName("OpenFileGDB");
        System.out.println(driver.getName());
    }

    @Test
    public void testGdalImport() throws IOException {
        Importer.main(new String[]{"C:\\Users\\yanlo\\Desktop\\shuixi.properties", "F:\\Project\\山东国土测绘院\\实体查询\\shuixi.shp"
                , "UTF-8", ""});
    }

    @Test
    public void testImportXzm() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\BOUA6乡镇面.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\xzfwm.properties";
        Importer.main(new String[]{configFile, path, "CP936", ""});
    }

    @Test
    public void testImportDaolu() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\daolu.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\daolu.properties";
        Importer.main(new String[]{configFile, path, "CP936", ""});
    }

    @Test
    public void testImportPOI() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\poi.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\poi.properties";
        Importer.main(new String[]{configFile, path, "", ""});
    }

    @Test
    public void testImportShuixi() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\shuixi.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\shuixi.properties";
        Importer.main(new String[]{configFile, path, "CP936", ""});
    }

    @Test
    public void testReadXzm() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\BOUA6乡镇面.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\xzfwm.properties";
        List<Map<String, Object>> records = getRecords(path, configFile, "CP936");
        Files.write(Paths.get("C:\\Users\\yanlo\\Desktop\\xiangzhenmian.txt"), records.stream().map(Object::toString).collect(Collectors.toList()));
//        System.out.println(records);
        Importer.main(new String[]{configFile, path, "CP936", ""});
    }

    @Test
    public void testReadDaolu() throws IOException {
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\daolu.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\daolu.properties";
        List<Map<String, Object>> records = getRecords(path, configFile, "GBK");
        Files.write(Paths.get("C:\\Users\\yanlo\\Desktop\\daolu.txt"), records.stream().map(Object::toString).collect(Collectors.toList()));
//        System.out.println(records);
    }

    @Test
    public void testReadShuixi() {
        List<String> exceptionIds = Arrays.asList("SXM3865",
                "SXM4368",
                "SXM5848",
                "SXM6186",
                "SXM6399",
                "SXM6430",
                "SXM6435"
        );
        String path = "F:\\Project\\山东国土测绘院\\实体查询\\shuixi.shp";
        String configFile = "C:\\Users\\yanlo\\Desktop\\shuixi.properties";
        List<Map<String, Object>> records = getRecords(path, configFile, "CP936");
        records = records.stream().filter(m -> exceptionIds.contains(m.get("ENTIID6"))).collect(Collectors.toList());
        System.out.println(records);
    }

    private List<Map<String, Object>> getRecords(String path, String configFile, String charset) {
        List<Map<String, Object>> records = new ArrayList<>();
        try (ShapefileReader reader = new ShapefileReader(path, charset)) {
            Properties configs = new Properties();
            InputStream is = Files.newInputStream(Paths.get(configFile));
            configs.load(is);
            List<String> fields = Arrays.stream(configs.getProperty("fields.all").split(",")).map(String::trim).collect(Collectors.toList());
            String fieldUuid = configs.getProperty("fields.uuid");
            Feature feature;
            SpatialReference sr = reader.getSpatialReference();
            CoordinateTransformation transformation = null;
            if (sr.IsProjected() == 1) {
                SpatialReference geoSr = sr.CloneGeogCS();
                transformation = osr.CreateCoordinateTransformation(sr, geoSr);
            }
            while ((feature = reader.nextFeature()) != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("doc_id", fieldUuid == null ? UUID.randomUUID() : feature.GetFieldAsString(fieldUuid));
                for (String key : fields) {
                    Object value = feature.GetFieldAsString(key);
                    map.put(key, value);
                }
                Geometry geom = feature.GetGeometryRef();
                if (geom == null || geom.IsEmpty())
                    continue;
                if (geom.Is3D() == 1) {
                    geom.FlattenTo2D();
                }
                if (transformation != null)
                    geom.Transform(transformation);
                String geojson = geom.ExportToJson();
                JSONObject json = new JSONObject(geojson);
                Map<String, Object> geo = new HashMap<>();
                if (json.getString("type").equalsIgnoreCase("point")) {
                    geo.put("lon", json.getJSONArray("coordinates").get(0));
                    geo.put("lat", json.getJSONArray("coordinates").get(1));
                } else {
                    geo.put("type", json.get("type"));
                    geo.put("coordinates", json.get("coordinates"));
                }
                map.put("the_geom", geo);
                map.put("keywords", "");
                records.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
