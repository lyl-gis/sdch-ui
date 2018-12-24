package edu.zju.gis.sdch.service.impl;

import edu.zju.gis.sdch.mapper.CategoryMapper;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexMappingMapper;
import edu.zju.gis.sdch.model.Category;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/21
 */
@AllArgsConstructor
public class IndexServiceImpl implements IndexService {
    private ElasticSearchHelper helper;
    private CategoryMapper categoryMapper;
    private IndexMapper indexMapper;
    private IndexMappingMapper indexMappingMapper;

    @Override
    public boolean createCatetory(Category category) {
        return categoryMapper.insert(category) == 1;
    }

    @Override
    public boolean createIndex(String indice, String dtype, String geomType, String description, int shards
            , int replicas, String mappingSource) throws IOException {
        if (helper.createIfNotExist(indice, shards, replicas, mappingSource)) {
            if (indexMapper.selectByPrimaryKey(indice) == null) {
                Index index = new Index();
                index.setIndice(indice);
                index.setDtype(dtype);
                index.setShards(shards);
                index.setReplicas(replicas);
                index.setGeoType(geomType);
                index.setDescription(description);
                index.setCreateTime(new Date());
                int flag = indexMapper.insert(index);//更新索引创建信息到数据库
                return flag == 1;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteIndex(String indice) {
        if (helper.delete(indice)) {
            indexMappingMapper.deleteByIndice(indice);
            indexMapper.deleteByPrimaryKey(indice);
        }
        return true;
    }

    @Override
    public int upsert(String index, String type, Map<String, Map<String, Object>> docs) {
        return helper.upsert(index, type, docs);
    }

    @Override
    public boolean delete(String index) {
        return helper.delete(index);
    }

    @Override
    public long delete(String index, String type) {
        Client client = helper.getClient();
        return DeleteByQueryAction.INSTANCE.newRequestBuilder(client).source(index)
                .filter(QueryBuilders.matchQuery("dtype", type)).get().getDeleted();
    }

    @Override
    public boolean deleteDoc(String index, String docId) {
        return helper.delete(index, "_doc", docId);
    }
}
