package edu.zju.gis.sdch.service;

import edu.zju.gis.sdch.model.Category;

import java.io.IOException;
import java.util.Map;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/21
 */
public interface IndexService {
    boolean createCatetory(Category category);

    boolean createIndex(String indice, String dtype, String geomType, String description, int shards, int replicas, String mappingSource) throws IOException;

    boolean deleteIndex(String indice);

    /**
     * 构建索引（如果某索引存在，则执行更新）
     *
     * @param index 索引（相当于数据库）
     * @param type  索引类型（相当于表）
     * @param docs  索引数据记录（相当于表记录），文档ID->文档
     * @return 索引构建失败的记录数
     */
    int upsert(String index, String type, Map<String, Map<String, Object>> docs);

    boolean delete(String index);

    long delete(String index, String type);

    boolean deleteDoc(String index, String docId);

}
