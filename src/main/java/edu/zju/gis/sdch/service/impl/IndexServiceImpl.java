package edu.zju.gis.sdch.service.impl;

import edu.zju.gis.sdch.mapper.CategoryMapper;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexMappingMapper;
import edu.zju.gis.sdch.mapper.IndexTypeMapper;
import edu.zju.gis.sdch.model.Category;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.model.IndexMapping;
import edu.zju.gis.sdch.model.IndexType;
import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.util.Contants;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    private IndexTypeMapper indexTypeMapper;
    private IndexMappingMapper indexMappingMapper;
    @Override
    public boolean createCategory(Category category) {
        return categoryMapper.insert(category) == 1;
    }

    @Override
    public boolean createIndex(Index index) throws IOException {
        if (helper.createIfNotExist(index.getIndice(), index.getShards(), index.getReplicas())) {
            if (indexMapper.selectByPrimaryKey(index.getIndice()) == null) {
                return indexMapper.insert(index) == 1;
            }
            return true;
        }
        return false;
    }

    @Override
    public void upsertIndexType(IndexType indexType) {
        if (indexTypeMapper.selectByType(indexType.getIndice(), indexType.getDtype()) == null)
            indexTypeMapper.insertSelective(indexType);
        else
            indexTypeMapper.updateByPrimaryKeySelective(indexType);
    }

    @Override
    public List<IndexType> getIndexType(String indice) {
        return indexTypeMapper.selectByIndice(indice);
    }

    @Override
    public void addMapping(List<IndexMapping> mappings) {
        if (mappings.isEmpty())
            return;
        JSONObject source = new JSONObject();
        JSONObject properties = new JSONObject();
        source.put("properties", properties);
        for (IndexMapping mapping : mappings) {
            switch (mapping.getFieldType()) {
                case "integer":
                    properties.put(mapping.getFieldName(), new JSONObject().put("type", "integer").put("store", true));
                    break;
                case "long":
                    properties.put(mapping.getFieldName(), new JSONObject().put("type", "long").put("store", true));
                    break;
                case "float":
                    properties.put(mapping.getFieldName(), new JSONObject().put("type", "float").put("store", true));
                    break;
                case "string":
                    if (mapping.getAnalyzable())
                        properties.put(mapping.getFieldName(), new JSONObject().put("type", "text").put("store", true)
                                .put("analyzer", "ik_max_word").put("search_analyzer", "ik_smart")
                                .put("boost", mapping.getBoost()));
                    else {
                        String type = "text";
                        if (mapping.getFieldName().equalsIgnoreCase(Contants.ADMIN_CODE))
                            type = "keyword";
                        properties.put(mapping.getFieldName(), new JSONObject().put("type", type).put("store", true));
                    }
                    break;
            }
        }
        properties.put("dtype", new JSONObject().put("type", "keyword").put("store", true));
        properties.put("the_shape", new JSONObject().put("type", "geo_shape"));
        properties.put("the_point", new JSONObject().put("type", "geo_point"));
        helper.putMapping(mappings.get(0).getIndice(), "_doc", source.toString());
        for (IndexMapping mapping : mappings)
            if (indexMappingMapper.selectByField(mapping.getIndice(), mapping.getFieldName()) == null)
                indexMappingMapper.insert(mapping);
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
    public Index getByIndice(String indice) {
        return indexMapper.selectByPrimaryKey(indice);
    }

    @Override
    public List<IndexMapping> getMappingByIndice(String indice) {
        return indexMappingMapper.selectByIndice(indice);
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

    @Override
    public String[] getAnalyzable(String... indexNames) {
        List<IndexMapping> mappings = indexMappingMapper.selectByIndices(Arrays.asList(indexNames));
        return mappings.stream().filter(IndexMapping::getAnalyzable).map(IndexMapping::getFieldName).toArray(String[]::new);
    }

    @Override
    public List<Index> getIndices() {
        int count = indexMapper.getCount();
        return indexMapper.selectByPage(0, count);
    }

    @Override
    public String[] getIndexNames() {
        return getIndices().stream().map(Index::getIndice).toArray(String[]::new);
    }

    @Override
    public Index getIndexByName(String indice) {
        return indexMapper.selectByPrimaryKey(indice);
    }

}
