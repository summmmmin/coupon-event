package com.example.couponevent.coupon.service;

import com.example.couponevent.coupon.kafka.CouponIssueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisKafkaCouponService {

    private static final String COUPON_COUNT_KEY = "coupon:count";
    private static final long MAX_COUPON_COUNT = 100L;

    private final StringRedisTemplate stringRedisTemplate;
    private final CouponIssueProducer couponIssueProducer;

    public void issue(Long userId) {
        Long increment = stringRedisTemplate.opsForValue()
                .increment(COUPON_COUNT_KEY);

        if (increment == null) {
            throw new IllegalStateException("쿠폰 발급 개수 증가 실패");
        }

        if (increment > MAX_COUPON_COUNT) {
            throw new CouponSoldOutException();
        }

        couponIssueProducer.sendIssueRequest(userId);
    }

    public void reset() {
        stringRedisTemplate.delete(COUPON_COUNT_KEY);
    }
}
