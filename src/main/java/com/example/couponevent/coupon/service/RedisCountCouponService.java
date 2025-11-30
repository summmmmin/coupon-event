package com.example.couponevent.coupon.service;

import com.example.couponevent.coupon.domain.Coupon;
import com.example.couponevent.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisCountCouponService {

    private static final String COUPON_COUNT_KEY = "coupon:count";
    private static final long MAX_COUPON_COUNT = 100L;

    private final StringRedisTemplate stringRedisTemplate;
    private final CouponRepository couponRepository;

    public void reset() {
        stringRedisTemplate.delete(COUPON_COUNT_KEY);
    }

    /**
     * Redis 증가 연산 기반 선착순 발급.
     * 1. Redis에서 발급 개수를 증가
     * 2. 증가 결과가 최대 개수 이하면 데이터베이스에 저장
     */
    @Transactional
    public void issue(Long userId) {
        Long currentIssued = stringRedisTemplate.opsForValue()
                .increment(COUPON_COUNT_KEY);

        if (currentIssued == null) {
            throw new IllegalStateException("쿠폰 발급 개수 증가에 실패했습니다.");
        }

        if (currentIssued > MAX_COUPON_COUNT) {
            // 이미 선착순이 끝난 경우, 데이터베이스에는 기록하지 않는다.
            throw new CouponSoldOutException();
        }

        Coupon coupon = new Coupon(userId);
        couponRepository.save(coupon);
    }

    @Transactional
    public void issueWithPossibleFailure(Long userId, boolean failAfterRedis) {
        Long currentIssued = stringRedisTemplate.opsForValue()
                .increment(COUPON_COUNT_KEY);

        if (currentIssued == null) {
            throw new IllegalStateException("쿠폰 발급 개수 증가에 실패했습니다.");
        }

        if (currentIssued > MAX_COUPON_COUNT) {
            throw new CouponSoldOutException();
        }

        if (failAfterRedis) {
            throw new RuntimeException("데이터베이스 저장 실패가 발생했다고 가정합니다.");
        }

        Coupon coupon = new Coupon(userId);
        couponRepository.save(coupon);
    }
}
