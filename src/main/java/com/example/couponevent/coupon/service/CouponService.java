package com.example.couponevent.coupon.service;

import com.example.couponevent.coupon.domain.Coupon;
import com.example.couponevent.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private static final long MAX_COUPON_COUNT = 100L;

    private final CouponRepository couponRepository;

    @Transactional
    public void issue(Long userId) {
        // 1. 현재 발급 수 조회
        long count = couponRepository.count();

        if (count >= MAX_COUPON_COUNT) {
            throw new CouponSoldOutException();
        }

        // 2. 아직 100 미만이면 발급
        Coupon coupon = new Coupon(userId);
        couponRepository.save(coupon);

    }
}
