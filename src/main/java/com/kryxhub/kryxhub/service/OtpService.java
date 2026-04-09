package com.kryxhub.kryxhub.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.secureRandom = new SecureRandom();
    }

    public String generateAndStoreOtp(String email, String purpose) {
        int otpNum = 100000 + secureRandom.nextInt(900000);
        String otpCode = String.valueOf(otpNum);

        String redisKey = "OTP:" + purpose.toUpperCase() + ":" + email;

        redisTemplate.opsForValue().set(redisKey, otpCode, Duration.ofMinutes(10));

        return otpCode;
    }

    public boolean validateOtp(String email, String purpose, String inputOtp) {
        String redisKey = "OTP:" + purpose.toUpperCase() + ":" + email;

        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            redisTemplate.delete(redisKey);
            return true;
        }

        return false;
    }
}