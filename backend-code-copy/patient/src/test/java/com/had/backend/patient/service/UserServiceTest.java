package com.had.backend.patient.service;

import com.had.backend.patient.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;
    @Test
    public void checkNullUserByAadhar() {
        String targetAadhar = "123";
        User user = userService.findUserByAadhar(targetAadhar);
        System.out.println("user = " + user);
    }
    
    @Test
    public void checkNonNullUserByAadhar() {
        String targetAadhar = "888888";
        User user = userService.findUserByAadhar(targetAadhar);
        System.out.println("user = " + user);
    }
}
