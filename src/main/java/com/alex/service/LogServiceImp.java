package com.alex.service;


import com.alex.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LogServiceImp implements LogService {
    private final Logger logger = LoggerFactory.getLogger(Application.LOGGED_MSG_LOGGER);

    @Autowired
    private ElasticsearchBulkService bulkService;


    @Override
    public void log(String category, Object message) {
        bulkService.indexingFromRaw(category, message);
        logger.info("Add to category: {}, source: {}", category, message);
    }
}
