package com.example.couponevent.coupon.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueProducer {

    private static final String TOPIC_NAME = "coupon.issue";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendIssueRequest(Long userId) {
        kafkaTemplate.send(TOPIC_NAME, userId.toString());
    }
}
