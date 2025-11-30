package com.example.couponevent.coupon;

import com.example.couponevent.coupon.domain.CouponRepository;
import com.example.couponevent.coupon.service.CouponSoldOutException;
import com.example.couponevent.coupon.service.RedisKafkaCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisKafkaCouponServiceTest {

    @Autowired
    RedisKafkaCouponService redisKafkaCouponService;

    @Autowired
    CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        redisKafkaCouponService.reset();
        couponRepository.deleteAll();
    }

    @Test
    void 카프카_기반_선착순_발급_최종_데이터베이스_발급_수는_100개를_초과하지않는다() throws Exception {
        int totalRequest = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(totalRequest);

        for (int i = 0; i < totalRequest; i++) {
            long userId = 30_000L + i;
            executorService.submit(() -> {
                try {
                    redisKafkaCouponService.issue(userId);
                } catch (CouponSoldOutException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Kafka Consumer가 데이터를 저장할 시간을 잠시 준다
        Thread.sleep(3000);

        long total = couponRepository.count();
        System.out.println("최종 발급 수 (Redis + Kafka) = " + total);

        assertThat(total).isEqualTo(100L);
    }
}
