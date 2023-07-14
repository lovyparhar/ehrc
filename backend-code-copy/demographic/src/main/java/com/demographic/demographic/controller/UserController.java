package com.demographic.demographic.controller;

import com.demographic.demographic.model.AddUserRequest;
import com.demographic.demographic.model.CheckUserRequest;
import com.demographic.demographic.model.UserResponse;
import com.demographic.demographic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-demographic")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/find-user")
    public ResponseEntity<UserResponse> checkUserExists(@RequestBody CheckUserRequest checkUserRequest) {
        return ResponseEntity.ok(userService.findUserByAadhar(checkUserRequest.getAadhar()));
    }

    @PostMapping("/add-user")
    public ResponseEntity<UserResponse> addUser(@RequestBody AddUserRequest addUserRequest) {
        return ResponseEntity.ok(userService.addUser(addUserRequest));
    }
}
