package com.alex.service;


import com.alex.Application;
import com.alex.dto.LoggedMsg;
import com.alex.dto.ProcessedMsg;
import com.alex.repository.LoggedMsgRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


public class Receiver {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private LoggedMsgRepository loggedMsgRepository;


    public void receiveMessage(String message) {
        loggedMsgRepository.save(createLogMsg(message));
        logger.info("receive msg: {}", message);
        Application.COUNT_DOWN_LATCH.countDown();
    }


    private LoggedMsg createLogMsg(String message) {
        try {
            ProcessedMsg processedMsg = objectMapper.readValue(message, ProcessedMsg.class);
            String category = processedMsg.getCategory();
            String jsonMsg = objectMapper.writeValueAsString(processedMsg.getMsg());
            return new LoggedMsg(category, jsonMsg);
        } catch (IOException e) {
            throw new IllegalStateException("For msg: " + message, e);
        }
    }
}
