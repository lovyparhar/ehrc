package com.had.backend.hospital.service;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.entity.User;
import com.had.backend.hospital.model.AuthResponse;
import com.had.backend.hospital.model.LoginRequest;
import com.had.backend.hospital.model.RegisterRequest;
import com.had.backend.hospital.repository.UserRepository;
import com.had.backend.hospital.utils.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .aadhar(request.getAadhar())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .build();
        try {
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .hospitalName(MessagingConfig.hospitalId)
                    .token(jwtToken)
                    .build();
        } catch (Exception exception) {
            return AuthResponse.builder().message("Duplicate User Found!").build();
        }
    }

    public AuthResponse login(LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getAadhar(),
//                        request.getPassword()
//                )
//        );
        UserDetails user = userRepository.findByAadhar(request.getAadhar())
                .orElseThrow();
        User userObject = (User)user;
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .aadhar(userObject.getAadhar())
                .email(userObject.getEmail())
                .firstname(userObject.getFirstname())
                .lastname(userObject.getLastname())
                .role(userObject.getRole().name())
                .hospitalName(MessagingConfig.hospitalId)
                .build();
    }
}
