package edu.zju.gis.config;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/13
 */
@Getter
@Setter
public class CommonSetting {
    private Properties properties;
    private String esName;
    private List<String> esHosts;
    private int esPort;
    private String esIndex;
    private int esShards;
    private int esReplicas;

    private CommonSetting(Properties properties) {
        this.properties = properties;
        esName = properties.getProperty("es.name", "elasticsearch");
        esHosts = Arrays.asList(properties.getProperty("es.hosts").split(","));
        esPort = Integer.parseInt(properties.getProperty("es.port", "9300"));
        esIndex = properties.getProperty("es.index");
        esShards = Integer.parseInt(properties.getProperty("es.number_of_shards", "4"));
        esReplicas = Integer.parseInt(properties.getProperty("es.number_of_replicas", "0"));
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public static CommonSetting getInstance() {
        return InstanceHolder.setting;
    }

    private static class InstanceHolder {
        static CommonSetting setting;

        static {
            Properties props = new Properties();
            try {
                InputStream is = ClassLoader.getSystemResourceAsStream("config.properties");
                props.load(is);
                setting = new CommonSetting(props);
            } catch (IOException e) {
                throw new RuntimeException("读取配置文件config.properties异常", e);
            }
        }
    }
}
