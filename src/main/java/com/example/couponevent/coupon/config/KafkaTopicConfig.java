package com.example.couponevent.coupon.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    private static final String TOPIC_NAME = "coupon.issue";

    @Bean
    public NewTopic couponIssueTopic() {
        return new NewTopic(TOPIC_NAME, 1, (short) 1);
    }
}