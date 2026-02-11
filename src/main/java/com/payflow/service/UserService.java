package com.payflow.service;

import com.payflow.dto.UserCreateRequest;
import com.payflow.dto.UserResponse;
import com.payflow.entity.User;
import com.payflow.entity.Wallet;
import com.payflow.exception.EmailAlreadyExistsException;
import com.payflow.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user)
    {
        Wallet wallet=new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);
        return userRepository.save(user);
    }

    public UserResponse createUser(UserCreateRequest request) {

        log.info("User creation request | email={}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("User creation failed - email already exists | email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);

        User savedUser = userRepository.save(user);

        log.info("User created successfully | userId={}", savedUser.getId());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

}
