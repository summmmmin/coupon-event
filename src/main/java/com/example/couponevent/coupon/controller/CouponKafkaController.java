package com.example.couponevent.coupon.controller;

import com.example.couponevent.coupon.service.RedisKafkaCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponKafkaController {

    private final RedisKafkaCouponService redisKafkaCouponService;

    @PostMapping("/issue-kafka")
    public ResponseEntity<String> issue(@RequestParam Long userId) {
        redisKafkaCouponService.issue(userId);
        return ResponseEntity.ok("요청이 정상적으로 처리되었습니다");
    }
}
