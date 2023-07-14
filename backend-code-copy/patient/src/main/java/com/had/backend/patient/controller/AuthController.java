package com.had.backend.patient.controller;

import com.had.backend.patient.entity.User;
import com.had.backend.patient.model.UserDemographicResponse;
import com.had.backend.patient.model.*;
import com.had.backend.patient.service.AuthService;
import com.had.backend.patient.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OTPService otpService;
    Map<String, String> VerifiedNumbers = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    private WebClient webClient;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        if(VerifiedNumbers.containsKey(request.getPhoneNumber())) {
            VerifiedNumbers.remove(request.getPhoneNumber());
            return ResponseEntity.ok(authService.register(request));

        }
        AuthResponse authResponse = new AuthResponse();
        return new ResponseEntity<>(authResponse, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Function just for checking the working of the web socket
    @GetMapping("/notifyFront")
    public String mfront() {
//        this.messagingTemplate.convertAndSend("/notify", new GenericMessage("Notification", "Payload"));
        return "Message Sent to front end.";
    }

    @PostMapping("/send-otp-update-password")
    public ResponseEntity<GenericMessage> sendOTP(@RequestBody SmsRequest smsRequest) {
        Boolean response = otpService.sendOTP(smsRequest);
        if (!response) {
            return new ResponseEntity<>(new GenericMessage("OTP Sending Failed, User Not Found", "Please make sure the user is valid."), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new GenericMessage("OTP Sent Successfully!", "The otp has been sent. Please submit this otp on the following screen to reset the password."), HttpStatus.OK);
    }

    @PatchMapping("/verify-otp-update-password")
    public ResponseEntity<GenericMessage> verifyOTP(@RequestBody updatePasswordRequest verifyRequest) {
        Boolean response = otpService.verifyOTPupdatePassword(verifyRequest);
        if (response) {
            return new ResponseEntity<>(new GenericMessage("OTP Verified!", "The otp is verified and the password is reset successfully."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new GenericMessage("OTP Verification Failed!", "Could not verify the otp."), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<GenericMessage> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Boolean response = authService.changePassword(changePasswordRequest.getPassword());

        if (response) {
            return new ResponseEntity<>(new GenericMessage("Password Change Successful!", "The password has been changed successfully, you can now log in with the new password."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new GenericMessage("Password Change was not Successful!", "The password could not be changed due to some reason. Try logging in again."), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/get-user-demographic")
    public ResponseEntity<GenericMessage> getDemographic(@RequestBody CheckUserDemographic checkUserDemographic , @RegisteredOAuth2AuthorizedClient("patient-client-client-credentials") OAuth2AuthorizedClient authorizedClient){
        UserDemographicResponse userDemographicResponse = this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/find-user")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .body(Mono.just(checkUserDemographic), CheckUserDemographic.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();

        if( userDemographicResponse.getMessage() == null){
            SmsRequest smsRequest = new SmsRequest(userDemographicResponse.getPhoneNumber(),userDemographicResponse.getAadhar());
            if(otpService.genericSendOTP(smsRequest)) {
                return new ResponseEntity<>(new GenericMessage("OTP Sent Successfully!,", "The otp has been sent"), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new GenericMessage("OTP Sending Failed,", "Please make sure the number is valid."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new GenericMessage("User Not Found", "Please make sure the aadhar is valid."), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/send-generic-otp")
    public ResponseEntity<GenericMessage> sendGenericOTP(@RequestBody SmsRequest smsRequest) {
        Boolean response = otpService.genericSendOTP(smsRequest);
        if (!response) {
            return new ResponseEntity<>(new GenericMessage("OTP Sending Failed,", "Please make sure the number is valid."), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new GenericMessage("OTP Sent Successfully!", "The otp has been sent"), HttpStatus.OK);
    }

    @PostMapping("/verify-generic-otp")
    public ResponseEntity<UserDemographicResponse> verifyGenericOTP(@RequestBody verifyOTPRequest verifyRequest) {
        if(verifyRequest.getPhoneNumber() == null) {
            CheckUserDemographic checkUserDemographic = new CheckUserDemographic(verifyRequest.getAadhar());
            UserDemographicResponse userDemographicResponse = this.webClient
                    .post()
                    .uri("http://127.0.0.1:7001/user-demographic/find-user")
                    .attributes(clientRegistrationId("patient-client-client-credentials"))
                    .body(Mono.just(checkUserDemographic), CheckUserDemographic.class)
                    .retrieve()
                    .bodyToMono(UserDemographicResponse.class)
                    .block();
            verifyRequest.setPhoneNumber(userDemographicResponse.getPhoneNumber());
            Boolean response = otpService.genericvVerifyOTP(verifyRequest);
            if(response) {
                VerifiedNumbers.put(verifyRequest.getPhoneNumber(), "verified");
                return new ResponseEntity<>(userDemographicResponse, HttpStatus.OK);
            }
            else {
//                userDemographicResponse = UserDemographicResponse.builder().build();
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
        else {
            UserDemographicResponse userDemographicResponse = UserDemographicResponse.builder().build();
            Boolean response = otpService.genericvVerifyOTP(verifyRequest);
            if (response) {
                VerifiedNumbers.put(verifyRequest.getPhoneNumber(), "verified");
                return new ResponseEntity<>(userDemographicResponse,HttpStatus.OK);
            } else {
                return new ResponseEntity<>(userDemographicResponse, HttpStatus.NOT_FOUND);
            }
        }
    }
}
