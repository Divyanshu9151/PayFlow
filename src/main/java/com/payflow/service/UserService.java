package com.payflow.service;

import com.payflow.dto.UserCreateRequest;
import com.payflow.dto.UserResponse;
import com.payflow.entity.User;
import com.payflow.entity.Wallet;
import com.payflow.exception.EmailAlreadyExistsException;
import com.payflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public UserResponse createUser(UserCreateRequest request)
    {
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        Wallet wallet=new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);
        if (userRepository.findByEmail(request.getEmail()).isPresent())
        {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        User savedUser=userRepository.save(user);
        return new UserResponse(
                savedUser.getId()
        ,savedUser.getName()
        ,savedUser.getEmail());
    }
}
