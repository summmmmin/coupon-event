package com.example.couponevent.coupon;

import com.example.couponevent.coupon.domain.CouponRepository;
import com.example.couponevent.coupon.service.CouponSoldOutException;
import com.example.couponevent.coupon.service.RedisCountCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisCountCouponServiceTest {

    @Autowired
    RedisCountCouponService redisCountCouponService;

    @Autowired
    CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
        redisCountCouponService.reset();
    }

    @Test
    void 증가_연산_방식에서도_최종_발급_수는_100장을_초과하지_않는다() throws Exception {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = 20_000L + i;
            executorService.submit(() -> {
                try {
                    redisCountCouponService.issue(userId);
                } catch (CouponSoldOutException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long total = couponRepository.count();
        System.out.println("최종 발급 수 (증가 연산 방식) = " + total);

        assertThat(total).isEqualTo(100L);
    }
}
