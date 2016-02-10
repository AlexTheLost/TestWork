package com.alex.service;


import com.alex.Application;
import com.alex.config.AmqpConfiguration;
import com.alex.dto.ProcessedMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LogServiceImp implements LogService {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Override
    public void log(String category, Object message) {
        try {
            String msg = objectMapper.writeValueAsString(new ProcessedMsg(category, message));
            rabbitTemplate.convertAndSend(AmqpConfiguration.QUEUE_NAME, msg);
            logger.info("send msg: {}", msg);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Category: " + category + "; message: " + message, e);
        }
    }
}
