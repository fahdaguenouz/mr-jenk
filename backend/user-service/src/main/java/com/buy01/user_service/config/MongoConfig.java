package com.buy01.user_service.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    
     @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        // Forces the driver to use your URI (username/password/authSource included)
        return MongoClients.create(mongoUri);
    }


    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "user_db"); 
    }
}