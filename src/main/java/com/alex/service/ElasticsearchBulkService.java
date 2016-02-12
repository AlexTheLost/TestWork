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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class ElasticsearchBulkService {
    public final AtomicLong counter = new AtomicLong(0);

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchBulkService.class);

    private final BulkProcessor bulkProcessor;

    private Client client;

    @Autowired
    private DelayedLoggingService delayedLoggingService;


    @Autowired
    public ElasticsearchBulkService(Client client, Environment environment) {
        this.client = client;
        bulkProcessor = BulkProcessor.builder(client, new Listener()).setBulkActions(readBulkActions(environment)).build();
    }


    public void indexingFromRaw(String index, Object rawSource) {
        try {
            bulkProcessor.add(client.prepareIndex(index, "log_msg").setSource(jsonBuilder()
                    .startObject().field("data", rawSource).endObject()).request());
        } catch (IOException e) {
            throw new IllegalStateException("Index: " + index + "; source: " + rawSource, e);
        }
    }

    public void indexingFromJson(String index, String jsonSource) {
        bulkProcessor.add(new IndexRequest(index, "log_msg").source(jsonSource));
    }

    private int readBulkActions(Environment environment) {
        return Integer.parseInt(environment.getProperty("elasticsearch.bulkActions"));
    }

    private class Listener implements BulkProcessor.Listener {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            if (response.hasFailures())
                logger.error(response.buildFailureMessage());
            counter.addAndGet(request.numberOfActions());
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            logger.warn("Unsuccessful indexing.", failure);
            handleBulkFailure(request);
        }
    }

    private void handleBulkFailure(BulkRequest request) {
        Iterator<ActionRequest> it = request.requests().iterator();
        while (it.hasNext()) {
            IndexRequest indexRequest = (IndexRequest) it.next();
            String index = indexRequest.index();
            String source = indexRequest.source().toUtf8();
            delayedLoggingService.toAmqpBroker(index, source);
        }
    }
}
