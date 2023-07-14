package com.had.backend.hospital.controller;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.model.*;
import com.had.backend.hospital.service.AuthService;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private WebClient webClient;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/get-user-demographic")
    public UserDemographicResponse getDemographic(@RequestBody CheckUserDemographic checkUserDemographic , @RegisteredOAuth2AuthorizedClient("hospital-client-client-credentials") OAuth2AuthorizedClient authorizedClient){
        UserDemographicResponse userDemographicResponse = this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/find-user")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .body(Mono.just(checkUserDemographic), CheckUserDemographic.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();
        userDemographicResponse.setPhoneNumber(null);
        return userDemographicResponse;
    }

}
