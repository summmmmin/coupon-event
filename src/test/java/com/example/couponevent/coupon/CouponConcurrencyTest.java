package com.example.couponevent.coupon;

import com.example.couponevent.coupon.domain.Coupon;
import com.example.couponevent.coupon.domain.CouponRepository;
import com.example.couponevent.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponConcurrencyTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        couponRepository.deleteAll();

        for (int i = 0; i < 99; i++) {
            couponRepository.save(new Coupon((long) i));
        }
    }

    @Test
    void 동시에_여러요청이_들어오면_100장을_초과해서_발급될_수_있다() throws Exception {
        int threadCount = 10; // 10명이 거의 동시에 마지막 쿠폰
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = 1000L + i;
            executorService.submit(() -> {
                try {
                    couponService.issue(userId);
                } catch (Exception e) {
                    //예외
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long total = couponRepository.count();
        System.out.println("최종 발급 수 = " + total);

        // 여러 번 실행해보면 100을 넘는 값이 나오는 경우 나옴
        assertThat(total).isGreaterThan(100L);
    }
}
