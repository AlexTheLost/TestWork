package com.alex.service;


import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

//import static org.elasticsearch.common.xcontent.XContentFactory
@Service
public class ElasticsearchBulkService {
    private final Logger logger = LoggerFactory.getLogger(ElasticsearchBulkService.class);

    private final BulkProcessor bulkProcessor;

    @Autowired
    private DelayedLoggingService delayedLoggingService;

    private Client client;

    private final AtomicInteger counter = new AtomicInteger();

    public void indexing(String index, Object source) {
        try {
            bulkProcessor.add(client.prepareIndex(index, "log_msg").setSource(jsonBuilder()
                    .startObject().field("data", source).endObject()).request());
        } catch (IOException e) {
            throw new IllegalStateException("Index: " + index + "; source: " + source, e);
        }
    }

    public void indexingFromJson(String index, String jsonSource) {
        bulkProcessor.add(new IndexRequest(index, "log_msg").source(jsonSource));
    }


    @Autowired
    public ElasticsearchBulkService(Client client) {
        this.client = client;
        bulkProcessor = BulkProcessor.builder(client, new Listener()).setBulkActions(10).build();
    }

    private class Listener implements BulkProcessor.Listener {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            if (response.hasFailures())
                logger.error(response.buildFailureMessage());
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            logger.info("Unsuccessful indexing.", failure);
            Iterator<ActionRequest> it = request.requests().iterator();
            while (it.hasNext()) {
                IndexRequest indexRequest = (IndexRequest) it.next();
                String index = indexRequest.index();
                String source = indexRequest.source().toUtf8();
                delayedLoggingService.toAmqpBroker(index, source);
            }
        }
    }
}
