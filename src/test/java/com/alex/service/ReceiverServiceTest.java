package com.alex.service;

import com.alex.dto.UnprocessedMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReceiverServiceTest {
    @Mock
    private ElasticsearchBulkService bulkService;

    @InjectMocks
    private ReceiverService receiverService;

    @Test
    public void testReceiveMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String testIndex = "testIndex";
        String testSource = "testSource";
        int count = 10;
        for (int i = 0; i < count; i++) {
            String msg = mapper.writeValueAsString(new UnprocessedMsg(testIndex, testSource));
            receiverService.receiveMessage(msg);
        }
        verify(bulkService, times(count)).indexingFromJson(testIndex, testSource);
    }
}