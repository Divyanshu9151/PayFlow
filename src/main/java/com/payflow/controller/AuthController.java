package com.payflow.controller;

import com.payflow.dto.LoginRequest;
import com.payflow.dto.LoginResponse;
import com.payflow.dto.UserCreateRequest;
import com.payflow.dto.UserResponse;
import com.payflow.entity.User;
import com.payflow.repository.UserRepository;
import com.payflow.security.JwtService;
import com.payflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        String email=request.email();
        String token= jwtService.generateToken(request.email());
       // User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );
        return ResponseEntity.ok(new LoginResponse(token));
//        return ResponseEntity.ok(
//                new LoginResponse(
//                        user.getId(),
//                        request.email(),
//                        "Login successful"
//                )
//        );
    }
}
