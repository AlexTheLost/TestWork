package com.alex.service;


import com.alex.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LogServiceImp implements LogService {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ElasticsearchBulkService bulkService;


    @Override
    public void log(String category, Object message) {
        bulkService.indexing(category, message);
        logger.info("Add to category: {}, source: {}", category, message);
    }
}
