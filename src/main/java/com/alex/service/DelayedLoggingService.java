package com.alex.service;


import com.alex.Application;
import com.alex.config.AmqpConfiguration;
import com.alex.dto.UnprocessedMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DelayedLoggingService {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RabbitTemplate rabbitTemplate;


    public void toAmqpBroker(String index, String source) {
        try {
            String msg = objectMapper.writeValueAsString(new UnprocessedMsg(index, source));
            rabbitTemplate.convertAndSend(AmqpConfiguration.QUEUE_NAME, msg);
            logger.info("send msg: {}", msg);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Index: " + index + "; source: " + source, e);
        }
    }
}
