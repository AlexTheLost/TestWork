package com.alex.config;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
public class ElasticsearchConfiguration {
    @Resource
    private Environment environment;

    @Bean
    public Client client() {
        return TransportClient.builder().settings(getSettings()).build().addTransportAddress(new InetSocketTransportAddress(getHost(), getPort()));
    }


    private Settings getSettings() {
        return Settings.builder().put("cluster.name", "elasticsearch").build();
    }


    private int getPort() {
        return Integer.parseInt(environment.getProperty("elasticsearch.port"));
    }


    private InetAddress getHost() {
        try {
            return InetAddress.getByName(environment.getProperty("elasticsearch.host"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
