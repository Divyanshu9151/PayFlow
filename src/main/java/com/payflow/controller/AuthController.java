package com.payflow.controller;

import com.payflow.dto.UserCreateRequest;
import com.payflow.dto.UserResponse;
import com.payflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }
}
