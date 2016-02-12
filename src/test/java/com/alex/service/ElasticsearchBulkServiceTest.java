package com.alex.service;

import com.alex.Application;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ElasticsearchBulkServiceTest {
    @Mock
    private DelayedLoggingService delayedLoggingService;

    @InjectMocks
    private ElasticsearchBulkService bulkService;

    @Test
    public void testHandleBulkFailure() throws Exception {
        BulkRequest request = new BulkRequest();
        String testIndex = "testIndex";
        String testSource = "testSource";
        int count = 10;
        for (int i = 0; i < count; i++) {
            request.add(new IndexRequest(testIndex).source(testSource));
        }
        bulkService.handleBulkFailure(request);
        verify(delayedLoggingService, times(count)).toAmqpBroker(testIndex, testSource);
    }
}