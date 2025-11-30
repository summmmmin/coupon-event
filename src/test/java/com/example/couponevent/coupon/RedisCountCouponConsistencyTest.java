package com.example.couponevent.coupon;

import com.example.couponevent.coupon.domain.CouponRepository;
import com.example.couponevent.coupon.service.RedisCountCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RedisCountCouponConsistencyTest {

    private static final String COUPON_COUNT_KEY = "coupon:count";

    @Autowired
    RedisCountCouponService redisCountCouponService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
        redisCountCouponService.reset();
    }

    @Test
    void 증가_연산_이후_데이터베이스_저장에_실패하면_Redis와_데이터베이스_정합성이_깨질_수_있다() {
        assertThrows(RuntimeException.class,
                () -> redisCountCouponService.issueWithPossibleFailure(1L, true));

        Long redisCount = Long.valueOf(stringRedisTemplate.opsForValue()
                .get(COUPON_COUNT_KEY));

        long dbCount = couponRepository.count();

        System.out.println("Redis 발급 수 = " + redisCount);
        System.out.println("데이터베이스 발급 수 = " + dbCount);

        // Redis는 1로 증가했지만, 데이터베이스에는 저장되지 않았으므로 0
        assertThat(redisCount).isEqualTo(1L);
        assertThat(dbCount).isEqualTo(0L);
    }
}
