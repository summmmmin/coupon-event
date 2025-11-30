package com.example.couponevent.coupon.kafka;

import com.example.couponevent.coupon.domain.Coupon;
import com.example.couponevent.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {

    private final CouponRepository couponRepository;

    @Transactional
    @KafkaListener(topics = "coupon.issue", groupId = "coupon-group")
    public void consume(String userIdValue) {
        Long userId = Long.valueOf(userIdValue);
        Coupon coupon = new Coupon(userId);
        couponRepository.save(coupon);
    }
}
