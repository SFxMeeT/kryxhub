package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.AdminUserDto;
import com.kryxhub.kryxhub.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<AdminUserDto> userFeed = userService.getAllUsersForAdmin(page, size);
        return ResponseEntity.ok(userFeed);
    }
}