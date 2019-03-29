package edu.zju.gis.sdch.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author yanlo yanlong_lee@qq.com
 * @version 1.0 2018/07/17
 */
public final class ElasticSearchHelper implements Closeable {
    private static final Logger log = LogManager.getLogger(ElasticSearchHelper.class);
    private Client client;

    public ElasticSearchHelper(List<String> hosts, int port, String clusterName) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();
        this.client = new PreBuiltTransportClient(settings);
        for (String host : hosts)
            ((PreBuiltTransportClient) this.client).addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
    }

    public Client getClient() {
        return client;
    }

    /**
     * 读取集群的健康状态
     */
    public ClusterHealthStatus getClusterHealth() {
        return client.admin().cluster().health(new ClusterHealthRequest()).actionGet().getStatus();
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引名数组
     * @return 当且仅当所有索引都存在时返回true
     */
    public boolean exists(String index) {
        return client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists();
    }

    public boolean exists(String index, String... types) {
        return client.admin().indices().prepareTypesExists(index).setTypes(types).get().isExists();
    }

    public boolean createIfNotExist(String index, int shards, int replicas) throws IOException {
        if (exists(index))
            return true;
        XContentBuilder settings = XContentFactory.jsonBuilder().startObject()
                .startObject("analysis")
                .startObject("analyzer")
                .startObject("ik_max_word").field("tokenizer", "ik_max_word").endObject()
                .startObject("ik_smart").field("tokenizer", "ik_smart").endObject()
                .endObject()
                .endObject()
                .field("number_of_shards", shards)
                .field("number_of_replicas", replicas)
                .endObject();
        return client.admin().indices().create(new CreateIndexRequest(index).settings(settings)).actionGet().isAcknowledged();
    }

    public boolean createIfNotExist(String index, int shards, int replicas, String mapping) throws IOException {
        if (!exists(index)) {
            XContentBuilder settings = XContentFactory.jsonBuilder().startObject()
                    .startObject("analysis")
                    .startObject("analyzer")
                    .startObject("ik_max_word").field("tokenizer", "ik_max_word").endObject()
                    .startObject("ik_smart").field("tokenizer", "ik_smart").endObject()
                    .endObject()
                    .endObject()
                    .field("number_of_shards", shards)
                    .field("number_of_replicas", replicas)
                    .endObject();
            CreateIndexRequest request = new CreateIndexRequest(index).settings(settings);
            boolean ack = client.admin().indices().create(request).actionGet().isAcknowledged();
            if (!ack)
                return false;
        }
        return putMapping(index, "_doc", mapping);
    }

    public boolean putMapping(String indice, String type, String source) {
        return client.admin().indices().preparePutMapping(indice).setType(type).setSource(source, XContentType.JSON).get().isAcknowledged();
    }

    public long getDocCount(String indice, String type) {
        SearchResponse response = client.prepareSearch(indice).setQuery(QueryBuilders.typeQuery(type)).setSize(0).get();
        return response.getHits().getTotalHits();
    }

    public List<String> getAsString(String index, String type, int offset, int size) throws ExecutionException, InterruptedException {
        SearchResponse response = client.prepareSearch(index).setTypes(type).setFrom(offset).setSize(size)
                .setTimeout(TimeValue.timeValueMinutes(8)).execute().get();
        SearchHits hits = response.getHits();
        List<String> map = new ArrayList<>();
        for (SearchHit hit : hits) {
            map.add(hit.getSourceAsString());
        }
        return map;
    }

    public List<Map<String, Object>> getAsMap(String index, String type, int offset, int size) throws ExecutionException, InterruptedException {
        SearchResponse response = client.prepareSearch(index).setTypes(type).setFrom(offset).setSize(size)
                .setTimeout(TimeValue.timeValueSeconds(10)).execute().get();
        SearchHits hits = response.getHits();
        List<Map<String, Object>> map = new ArrayList<>();
        for (SearchHit hit : hits) {
            map.add(hit.getSourceAsMap());
        }
        return map;
    }

    public boolean publish(String index, String type, String source) {
        return client.prepareIndex(index, type).setSource(source, XContentType.JSON).get().status() == RestStatus.CREATED;
    }

    public boolean publish(String index, String type, String id, String source) {
        return client.prepareIndex(index, type, id).setSource(source, XContentType.JSON).get().status() == RestStatus.CREATED;
    }

    /**
     * 发布索引
     *
     * @param type    索引类型
     * @param kvIdDoc id=>json doc
     * @return 0-发布成功，大于0的数字表示发布失败的记录数量
     */
    public int publish(String index, String type, Map<String, String> kvIdDoc) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map.Entry<String, String> entry : kvIdDoc.entrySet()) {
            bulkRequest.add(client.prepareIndex(index, type, entry.getKey()).setSource(entry.getValue(), XContentType.JSON));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse) {
                if (response.isFailed())
                    error++;
            }
        }
        return error;
    }

    /**
     * @param type     索引类型
     * @param entities model类列表数据
     * @return 0-发布成功，大于0的数字表示发布失败的记录数量
     */
    public int publish(String index, String type, List<Map<String, Object>> entities) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map<String, Object> entity : entities) {
            String docId = entity.get("doc_id").toString();
            entity.remove("doc_id");
            bulkRequest.add(client.prepareIndex(index, type, docId).setSource(entity));
            if (bulkRequest.numberOfActions() >= 1000) {
                BulkResponse bulkResponse = bulkRequest.get();
                if (bulkResponse.hasFailures()) {
                    for (BulkItemResponse response : bulkResponse) {
                        if (response.isFailed()) {
                            error++;
                            log.error(response.getFailure());
                        }
                    }
                }
                bulkRequest = client.prepareBulk();
            }
        }
        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse response : bulkResponse) {
                    if (response.isFailed()) {
                        error++;
                        log.error(response.getFailure());
                    }
                }
            }
        }
        return error;
    }

    public int publish(String index, String type, Iterable<JSONObject> jsons) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (JSONObject entity : jsons) {
            String docId = entity.get("doc_id").toString();
            entity.remove("doc_id");
            bulkRequest.add(client.prepareIndex(index, type, docId).setSource(entity.toString(), XContentType.JSON));
            if (bulkRequest.numberOfActions() >= 1000) {
                BulkResponse bulkResponse = bulkRequest.get();
                if (bulkResponse.hasFailures()) {
                    for (BulkItemResponse response : bulkResponse) {
                        if (response.isFailed()) {
                            error++;
                            log.error(response.getFailure());
                        }
                    }
                }
                bulkRequest = client.prepareBulk();
            }
        }
        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse response : bulkResponse) {
                    if (response.isFailed()) {
                        error++;
                        log.error(response.getFailure());
                    }
                }
            }
        }
        return error;
    }

    /**
     * 更新索引
     *
     * @param type    索引类型
     * @param id      文档ID
     * @param jsonDoc JSON字符串类型的更新内容（只包含更新部分）
     */
    public boolean update(String index, String type, String id, String jsonDoc) {
        return client.prepareUpdate(index, type, id).setDoc(jsonDoc, XContentType.JSON).get().getGetResult().isExists();
    }

    /**
     * 更新索引
     *
     * @param type    索引类型
     * @param kvIdDoc id=>json doc
     */
    public int update(String index, String type, Map<String, String> kvIdDoc) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map.Entry<String, String> entry : kvIdDoc.entrySet()) {
            bulkRequest.add(client.prepareUpdate(index, type, entry.getKey()).setDoc(entry.getValue(), XContentType.JSON));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse) {
                if (response.isFailed())
                    error++;
            }
        }
        return error;
    }

    /**
     * 更新索引
     *
     * @param type    索引类型
     * @param kvIdDoc id=>json字符串或Map
     */
    public int updateFromJson(String index, String type, Map<String, String> kvIdDoc) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map.Entry<String, String> entry : kvIdDoc.entrySet()) {
            bulkRequest.add(client.prepareUpdate(index, type, entry.getKey()).setDoc(entry.getValue(), XContentType.JSON));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse) {
                if (response.isFailed())
                    error++;
            }
        }
        return error;
    }

    /**
     * 更新索引
     *
     * @param type    索引类型
     * @param kvIdDoc id=>json字符串或Map
     */
    public int updateFromMap(String index, String type, Map<String, Map<String, Object>> kvIdDoc) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map.Entry<String, Map<String, Object>> entry : kvIdDoc.entrySet()) {
            bulkRequest.add(client.prepareUpdate(index, type, entry.getKey()).setDoc(entry.getValue()));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse) {
                if (response.isFailed())
                    error++;
            }
        }
        return error;
    }

    public boolean upsert(String index, String type, String id, Map<String, Object> doc) {
        return client.prepareUpdate(id, type, id).setUpsert(doc).get().getGetResult().isExists();
    }

    public int upsert(String index, String type, Map<String, Map<String, Object>> kvIdDoc) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        int error = 0;
        for (Map.Entry<String, Map<String, Object>> entry : kvIdDoc.entrySet()) {
            IndexRequest indexRequest = new IndexRequest(index, type, entry.getKey()).source(entry.getValue());
            bulkRequest.add(client.prepareUpdate(index, type, entry.getKey()).setDoc(entry.getValue()).setUpsert(indexRequest));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse) {
                if (response.isFailed())
                    error++;
            }
        }
        return error;
    }

    public boolean delete(String... indices) {
        return client.admin().indices().prepareDelete(indices).get().isAcknowledged();
    }

    /**
     * @param indice 索引名
     * @param type   索引type
     * @return 删除的记录数
     */
    public long delete(String indice, String type) {
        return DeleteByQueryAction.INSTANCE.newRequestBuilder(client).source(indice)
                .filter(QueryBuilders.typeQuery(type)).get().getDeleted();
    }


    public boolean delete(String index, String type, String id) {
        return client.prepareDelete(index, type, id).get().getResult() == DocWriteResponse.Result.DELETED;

    }

    public int delete(String index, String type, List<String> idList) {
        int error = 0;
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        idList.forEach(id -> bulkRequest.add(client.prepareDelete(index, type, id)));
        BulkResponse bulkResponse = bulkRequest.get();
        for (BulkItemResponse response : bulkResponse) {
            if (response.isFailed())
                error++;
        }
        return error;
    }

    public String getDetail(String index, String type, String docId) {
        GetResponse getResponse = client.prepareGet(index, type, docId).get();
        return getResponse.getSourceAsString();
    }

    public void close() {
        client.close();
    }
}
