package com.alex.service;


public interface LogService {
    // category - log category, the same as ElasticSearch index ID
    // message - log message, blindly serialize to JSON, add timestamp and store as is.
    void log(String category, Object message);
}
