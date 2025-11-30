package com.example.couponevent.coupon.service;

import com.example.couponevent.coupon.domain.Coupon;
import com.example.couponevent.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisPreloadCouponService {

    private static final String COUPON_STOCK_KEY = "coupon:stock:preload";

    private final StringRedisTemplate stringRedisTemplate;
    private final CouponRepository couponRepository;

    /**
     * 쿠폰을 미리 Redis에 채워 넣는 메서드.
     * 테스트 또는 이벤트 시작 전에 실행한다고 가정.
     */
    public void prepareCoupons(long count) {
        stringRedisTemplate.delete(COUPON_STOCK_KEY);
        for (int i = 0; i < count; i++) {
            stringRedisTemplate.opsForList().leftPush(COUPON_STOCK_KEY, "TOKEN");
        }
    }

    /**
     * 선착순 쿠폰 발급.
     * Redis 리스트에서 토큰을 하나 꺼내서, 꺼내기에 성공한 경우에만 데이터베이스에 저장한다.
     */
    @Transactional
    public void issue(Long userId) {
        String token = stringRedisTemplate.opsForList().leftPop(COUPON_STOCK_KEY);

        if (token == null) {
            throw new CouponSoldOutException();
        }

        Coupon coupon = new Coupon(userId);
        couponRepository.save(coupon);
    }
}
