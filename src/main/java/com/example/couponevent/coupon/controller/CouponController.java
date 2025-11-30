package com.example.couponevent.coupon.controller;

import com.example.couponevent.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/issue")
    public ResponseEntity<String> issue(@RequestParam Long userId) {
        couponService.issue(userId);
        return ResponseEntity.ok("쿠폰 발급 성공");
    }
}
