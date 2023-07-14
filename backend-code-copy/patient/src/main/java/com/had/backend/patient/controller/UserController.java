package com.had.backend.patient.controller;

import com.had.backend.patient.model.GenericMessage;
import com.had.backend.patient.model.UpdatePatientDetailsRequest;
import com.had.backend.patient.service.UserDetailsServiceImpl;
import com.had.backend.patient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/helloAdmin")
    public String helloAdmin() {
        return "Hello Admin";
    }


    @PostMapping("/update-patient-details")
    public ResponseEntity<GenericMessage> updatePatientDetails(@RequestBody UpdatePatientDetailsRequest updatePatientDetailsRequest) {
        System.out.println("updatePatientDetailsRequest.toString() = " + updatePatientDetailsRequest.toString());
        Boolean response = userService.updatePatientDetails(updatePatientDetailsRequest);
        if(response) {
            return new ResponseEntity<>(new GenericMessage("User Details Updated!", "User Details Updated!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericMessage("User Details Update Failed!", "Internal Server Error, check logs!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
