package com.had.backend.hospital.controller;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.entity.ReceivedRecords;
import com.had.backend.hospital.entity.SentConsentRequest;
import com.had.backend.hospital.model.AddRecordRequest;
import com.had.backend.hospital.model.GenericMessage;
import com.had.backend.hospital.model.RequestConsentObject;
import com.had.backend.hospital.model.RequestDataObject;
import com.had.backend.hospital.service.ConsentService;
import com.had.backend.hospital.service.DcotorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    private DcotorServices doctorServices;

    @Autowired
    private ConsentService consentService;

    @GetMapping("/hello")
    public String helloDoctor() {
        return "Hello Doctor";
    }

    @GetMapping("/resource")
    public String resource() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String res = auth.getPrincipal().toString() + '\n';
        res += auth.getAuthorities().toString() + '\n';
        res += auth.getDetails().toString() + '\n';

        return res;
    }

    @PostMapping("/addrecord")
    public ResponseEntity<GenericMessage> addRecord(@RequestBody AddRecordRequest patientRecord) {
//        System.out.println("Reached");
        Boolean response = doctorServices.addRecord(patientRecord);
        if(response) {
            return new ResponseEntity<>(
                    new GenericMessage("Record Added Successfully!", "Record Added Successfully!"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericMessage("Record Addition Failed.", "Internal Server Error, check logs!"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/request-consent")
    public ResponseEntity<GenericMessage> requestConsent(@RequestBody RequestConsentObject requestConsentObject) {
        requestConsentObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
        Boolean response = consentService.requestConsent(requestConsentObject);
        return new ResponseEntity<>(new GenericMessage("Consent request forwarded to patient",
                "You can access the data once the consent is approved."),
                HttpStatus.OK);
    }

    @GetMapping("/getPendingConsents")
    public List<SentConsentRequest> getPendingConsents() {
        List<SentConsentRequest> consentRequests = consentService.getPendingConsents();
        return consentRequests;
    }

    @GetMapping("/getApprovedConsents")
    public List<SentConsentRequest> getApprovedConsents() {
        List<SentConsentRequest> consentRequests = consentService.getApprovedConsents();
        return consentRequests;
    }

    @PostMapping("/request-data")
    public ResponseEntity<GenericMessage> requestData(@RequestBody RequestDataObject requestDataObject) {
        requestDataObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
        Boolean response = consentService.requestData(requestDataObject);
        if(response) {
            return new ResponseEntity<>(new GenericMessage("Valid Consent Object Found and Forwarded",
                    "Data will arrive soon."),
                    HttpStatus.OK);

        }
        return new ResponseEntity<>(new GenericMessage("No Consent found corresponding to data request",
                "Please generate a new consent request for getting this data."),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getRecords")
    public List<ReceivedRecords> getRecords() {
        List<ReceivedRecords> patientRecordList = consentService.getRecords();
        return patientRecordList;
    }

    @GetMapping("/clear-records")
    public ResponseEntity<GenericMessage> clearRecords() {
        consentService.clearRecords();
        return new ResponseEntity<>(new GenericMessage("Records cleared",
                "Records Cleared Successfully."),
                HttpStatus.OK);
    }
}
