package com.alex.config;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.Resource;


@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
@EnableElasticsearchRepositories(basePackages = "com.alex.repository")
public class ElasticsearchConfiguration {
    @Resource
    private Environment environment;


    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(client());
    }


    @Bean
    public Client client() {
        return new TransportClient(getSettings()).addTransportAddress(new InetSocketTransportAddress(getHost(), getPort()));
    }


    private Settings getSettings() {
        return ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
    }


    private int getPort() {
        return Integer.parseInt(environment.getProperty("elasticsearch.port"));
    }


    private String getHost() {
        return environment.getProperty("elasticsearch.host");
    }
}
