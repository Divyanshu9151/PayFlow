package com.payflow.controller;

import com.payflow.dto.*;
import com.payflow.entity.RefreshToken;
import com.payflow.entity.User;
import com.payflow.repository.RefreshTokenRepository;
import com.payflow.repository.UserRepository;
import com.payflow.security.JwtService;
import com.payflow.service.AuthService;
import com.payflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
          @Valid   @RequestBody RefreshRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
