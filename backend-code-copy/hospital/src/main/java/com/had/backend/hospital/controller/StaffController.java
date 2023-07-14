package com.had.backend.hospital.controller;

import com.had.backend.hospital.model.AddRecordRequest;
import com.had.backend.hospital.model.GenericMessage;
import com.had.backend.hospital.model.UserDemographicRequest;
import com.had.backend.hospital.model.UserDemographicResponse;
import com.had.backend.hospital.service.RecordServices;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private RecordServices recordServices;
    @Autowired
    private WebClient webClient;

    @GetMapping("/hello")
    public String helloDoctor() {
        return "Hello Staff";
    }

    @GetMapping("/resource")
    public String resource() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String res = auth.getPrincipal().toString() + '\n';
        res += auth.getAuthorities().toString() + '\n';
        res += auth.getDetails().toString() + '\n';

        return res;
    }

    @PostMapping("/staff-add-record")
    public ResponseEntity<GenericMessage> addRecord(@RequestBody AddRecordRequest patientRecord) {
//        System.out.println("Reached");
        Boolean response = recordServices.staffAddRecord(patientRecord);
        if(response) {
            return new ResponseEntity<>(
                    new GenericMessage("Record Added Successfully!", "Record Added Successfully!"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericMessage("Record Addition Failed.", "Internal Server Error, check logs!"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/add-user-demographic")
    public UserDemographicResponse addDemographic(@RequestBody UserDemographicRequest userDemographic , @RegisteredOAuth2AuthorizedClient("hospital-client-client-credentials") OAuth2AuthorizedClient authorizedClient) {
        return this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/add-user")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .body(Mono.just(userDemographic), UserDemographicRequest.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();
    }


}
