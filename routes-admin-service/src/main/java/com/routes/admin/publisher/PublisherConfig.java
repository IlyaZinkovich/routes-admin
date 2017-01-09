package com.routes.admin.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import static org.zeromq.ZMQ.PUB;
import static org.zeromq.ZMQ.context;

@Configuration
public class PublisherConfig {

    @Value("${zeromq.address}")
    private String address;

    @Value("${zeromq.ioThreads}")
    private int ioThreads;

    @Bean(destroyMethod = "term")
    public Context zmqContext() {
        return context(ioThreads);
    }

    @Bean(destroyMethod = "close")
    public Socket zmqPubSocket() {
        return zmqContext().socket(PUB);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}