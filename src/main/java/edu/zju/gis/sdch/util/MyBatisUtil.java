package edu.zju.gis.sdch.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/20
 */
public class MyBatisUtil {

    private static class InstanceHolder {
        private static SqlSessionFactory factory;

        static {
            // 得到配置文件流
            try (InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml")) {
                // 创建会话工厂，传入mybatis的配置文件信息
                factory = new SqlSessionFactoryBuilder().build(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return InstanceHolder.factory;
    }

    public static <T> T getMapper(Class<T> clazz) {
        return getSqlSessionFactory().openSession(true).getMapper(clazz);
    }

}
