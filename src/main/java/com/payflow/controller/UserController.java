package com.payflow.controller;

import com.payflow.dto.UserCreateRequest;
import com.payflow.dto.UserResponse;
import com.payflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService service)
    {
        this.userService=service;
    }

//this move to authController now
//    @PostMapping
//    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request)
//    {
//        UserResponse response=userService.createUser(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse>getUser(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
