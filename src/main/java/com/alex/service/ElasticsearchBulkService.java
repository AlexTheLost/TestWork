package com.alex.service;


import com.google.common.annotations.VisibleForTesting;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class ElasticsearchBulkService {
    // For test from console.
    public final AtomicLong counter = new AtomicLong(0);

    private static final String FIELD_NAME = "data";

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchBulkService.class);

    private BulkProcessor bulkProcessor;

    @Autowired
    private DelayedLoggingService delayedLoggingService;

    @Autowired
    private Client client;

    @Autowired
    private Environment environment;


    @PostConstruct
    private void init() {
        bulkProcessor = BulkProcessor.builder(client, new Listener()).setBulkActions(readBulkActions(environment)).build();
    }


    public void indexingFromRaw(String index, Object rawSource) {
        bulkProcessor.add(prepareRequestFromRaw(index, rawSource));
    }

    public void indexingFromJson(String index, String jsonSource) {
        bulkProcessor.add(prepareRequestFromJson(index, jsonSource));
    }

    private int readBulkActions(Environment environment) {
        return Integer.parseInt(environment.getProperty("elasticsearch.bulkActions"));
    }

    private IndexRequest prepareRequestFromJson(String index, String jsonSource) {
        return new IndexRequest(index, "log_msg").source(jsonSource);
    }

    private IndexRequest prepareRequestFromRaw(String index, Object rawSource) {
        try {
            XContentBuilder data = jsonBuilder().startObject().field(FIELD_NAME, rawSource).endObject();
            return client.prepareIndex(index, "log_msg").setSource(data).request();
        } catch (IOException e) {
            throw new IllegalStateException("Index: " + index + "; source: " + rawSource, e);
        }
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

    @VisibleForTesting
    public void handleBulkFailure(BulkRequest request) {
        Iterator<ActionRequest> it = request.requests().iterator();
        while (it.hasNext()) {
            IndexRequest indexRequest = (IndexRequest) it.next();
            String index = indexRequest.index();
            String source = indexRequest.source().toUtf8();
            delayedLoggingService.toAmqpBroker(index, source);
        }
    }
}
