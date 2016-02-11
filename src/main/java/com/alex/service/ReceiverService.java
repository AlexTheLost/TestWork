package com.alex.service;


import com.alex.Application;
import com.alex.dto.UnprocessedMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReceiverService {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ElasticsearchBulkService esBulkService;


    public void receiveMessage(String message) {
        try {
            UnprocessedMsg msg = objectMapper.readValue(message, UnprocessedMsg.class);
            esBulkService.indexingFromJson(msg.getIndex(), msg.getSource());
            logger.info("Received unprocessed message: {}", message);
        } catch (IOException e) {
            throw new IllegalStateException("For msg: " + message, e);
        }
    }

}
