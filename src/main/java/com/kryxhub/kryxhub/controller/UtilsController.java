package com.kryxhub.kryxhub.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    private final PasswordEncoder passwordEncoder;

    public UtilsController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/password")
    public String getHashPassword(@RequestBody String pwd) {
        return passwordEncoder.encode(pwd);
    }
}
