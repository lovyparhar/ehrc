package com.had.backend.patient.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.had.backend.patient.entity.ConsentRequest;
import com.had.backend.patient.entity.User;
import com.had.backend.patient.model.*;
import com.had.backend.patient.repository.UserRepository;
import com.had.backend.patient.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebClient webClient;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .aadhar(request.getAadhar())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .godFatherName(request.getGodFatherName())
                .godFatherNumber(request.getGodFatherNumber())
                .build();
        user.setAge();
        try {
            try {
                UserDemographicRequest userDemographicRequest = UserDemographicRequest
                        .builder()
                        .aadhar(request.getAadhar())
                        .lastName(request.getLastname())
                        .firstName(request.getFirstname())
                        .dateOfBirth(request.getDateOfBirth())
                        .phoneNumber(request.getPhoneNumber())
                        .godFatherNumber(request.getGodFatherNumber())
                        .godFatherName(request.getGodFatherName())
                        .build();
                UserDemographicResponse userDemographicResponse = addDemographic(userDemographicRequest);
                System.out.println("Added to demographic: " + userDemographicResponse.toString());
            }catch(Exception exception) {
                System.out.println(exception);
                System.out.println("Already Present In UserDemographic");
            }
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception exception) {
            return AuthResponse.builder().message("Duplicate User Found!").build();
        }
    }

    public UserDemographicResponse addDemographic(UserDemographicRequest userDemographic) {
        return this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/add-user")
                .attributes(clientRegistrationId("patient-client-client-credentials"))
                .body(Mono.just(userDemographic), UserDemographicRequest.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();
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
                .phoneNumber(userObject.getPhoneNumber())
                .age(userObject.getAge())
                .dateOfBirth(userObject.getDateOfBirth())
                .build();
    }

    public Boolean changePassword(@JsonProperty("password") String password) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String aadhar = user.getAadhar();
//        System.out.println("password = " + password);

        if(userRepository.existsByAadhar(aadhar)) {
            userRepository.findByAadhar(aadhar).ifPresent(user1 -> {
                user1.setPassword(passwordEncoder.encode(password));
                userRepository.save(user1);
            });
            return true;
        }

        return false;
    }
}
