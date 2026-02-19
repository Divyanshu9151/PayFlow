package com.payflow.service;

import com.payflow.dto.LoginRequest;
import com.payflow.dto.LoginResponse;
import com.payflow.dto.RefreshRequest;
import com.payflow.entity.RefreshToken;
import com.payflow.entity.User;
import com.payflow.repository.RefreshTokenRepository;
import com.payflow.repository.UserRepository;
import com.payflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String accessToken = jwtService.generateAccessToken(request.email());
        String refreshToken = jwtService.generateRefreshToken(request.email());

        User user = userRepository.findByEmail(request.email()).get();

        refreshTokenRepository.save(
                new RefreshToken(null, refreshToken,
                        jwtService.extractExpiration(refreshToken),
                        user)
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refresh(RefreshRequest request) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Refresh token expired");
        }

        String email = jwtService.extractUsername(request.refreshToken());
        if (!"REFRESH".equals(jwtService.extractTokenType(request.refreshToken())))
        {
            throw new RuntimeException(
                    "Invalid token Type"
            );
        }

        String newAccessToken = jwtService.generateAccessToken(email);

        return new LoginResponse(newAccessToken, request.refreshToken());
    }

}
