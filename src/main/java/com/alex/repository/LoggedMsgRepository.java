package com.alex.repository;


import com.alex.dto.LoggedMsg;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface LoggedMsgRepository extends ElasticsearchRepository<LoggedMsg, String> {
}
