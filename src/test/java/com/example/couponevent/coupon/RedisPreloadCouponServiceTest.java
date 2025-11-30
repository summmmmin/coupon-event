package com.example.couponevent.coupon;

import com.example.couponevent.coupon.service.CouponSoldOutException;
import com.example.couponevent.coupon.service.RedisPreloadCouponService;
import com.example.couponevent.coupon.domain.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisPreloadCouponServiceTest {

    @Autowired
    RedisPreloadCouponService redisPreloadCouponService;

    @Autowired
    CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
        redisPreloadCouponService.prepareCoupons(100L);
    }

    @Test
    void 미리_준비된_쿠폰은_100장을_초과하여_발급되지_않는다() throws Exception {
        int threadCount = 1000; // 동시에 1000명이 요청하는 상황
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = 10_000L + i;
            executorService.submit(() -> {
                try {
                    redisPreloadCouponService.issue(userId);
                } catch (CouponSoldOutException ignored) {
                    // 쿠폰 소진 예외는 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long total = couponRepository.count();
        System.out.println("최종 발급 수 (미리 발급 방식) = " + total);

        assertThat(total).isEqualTo(100L);
    }
}
