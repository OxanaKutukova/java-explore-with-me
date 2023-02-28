package ru.practicum.ewm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.stats.StatsClient;

@Configuration
public class ConfigStatsClient {

    @Value("${spring.application.name}")
    private  String application;
    @Value("${stats-service.uri}")
    private String statsServiceUri;


    @Bean
    public StatsClient init() {

        return new StatsClient(statsServiceUri, application, new ObjectMapper());
    }

}
