package edu.zju.gis.util;

import edu.zju.gis.sdch.util.ElasticSearchHelper;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Arrays;

public class EsTest {

    @Test
    public void deleteBySearch() throws UnknownHostException {
        ElasticSearchHelper helper = new ElasticSearchHelper(Arrays.asList("172.20.3.106"), 9300, "elasticsearch");
        Client client = helper.getClient();
//        SearchResponse response = client.prepareSearch("sdmap").setTypes("_doc")
//                .setQuery(QueryBuilders.matchQuery("dtype","f_poi"))
//                .setSize(20).get();
//        System.out.println(response.getHits().getTotalHits());

        long size = DeleteByQueryAction.INSTANCE.newRequestBuilder(client).source("sdmap")
                .filter(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("dtype", "fe_bsq"))
                        .mustNot(QueryBuilders.prefixQuery("lsid", "BSQ"))).get().getDeleted();
        System.out.println(size);
    }
}
