package com.alex.service;

import com.alex.config.AmqpConfiguration;
import com.alex.dto.UnprocessedMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DelayedLoggingServiceTest {
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DelayedLoggingService delayedLoggingService;

    @Test
    public void testToAmqpBroker() throws Exception {
        String testIndex = "testIndex";
        String testSource = "testSource";
        delayedLoggingService.toAmqpBroker(testIndex, testSource);
        final String msg = prepareMsg(testIndex, testSource);
        verify(rabbitTemplate).convertAndSend(AmqpConfiguration.QUEUE_NAME, msg);
    }

    private String prepareMsg(String testIndex, String testSource) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new UnprocessedMsg(testIndex, testSource));
    }
}