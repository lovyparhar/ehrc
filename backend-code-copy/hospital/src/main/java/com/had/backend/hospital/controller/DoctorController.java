package com.had.backend.hospital.controller;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.entity.ReceivedRecords;
import com.had.backend.hospital.entity.SentConsentRequest;
import com.had.backend.hospital.model.*;
import com.had.backend.hospital.service.ConsentService;
import com.had.backend.hospital.service.OTPService;
import com.had.backend.hospital.service.RecordServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    private ConsentService consentService;

    @Autowired
    private RecordServices recordServices;

    @Autowired
    private OTPService otpService;

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

    @PostMapping("/doctor-update-record")
    public ResponseEntity<GenericMessage> updateRecord(@RequestBody AddRecordRequest patientRecord) {
        System.out.println("patientRecord = " + patientRecord);
        Boolean response = recordServices.doctorUpdateRecord(patientRecord);
        if(response) {
            return new ResponseEntity<>(
                    new GenericMessage("Record Updated Successfully!", "Record Updated Successfully!"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericMessage("Record Update Failed.", "Internal Server Error, check logs!"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/doctor-add-record")
    public ResponseEntity<GenericMessage> addRecord(@RequestBody AddRecordRequest patientRecord) {
        System.out.println("patientRecord = " + patientRecord);
        Boolean response = recordServices.doctorAddRecord(patientRecord);
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

    @PostMapping("/update-consent")
    public ResponseEntity<GenericMessage> updateConsent(@RequestBody RequestConsentObject requestConsentObject) {
//        requestConsentObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
//        Boolean response = consentService.requestConsent(requestConsentObject);
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

    @PostMapping("/get-existing-active-consents")
    public ResponseEntity<List<SentConsentRequest>> getExistingActiveConsents(@RequestBody ConsentMessage consentMessage) {
        List<SentConsentRequest> consentRequests = consentService.getExistingActiveConsents(consentMessage);
//        System.out.println("consentRequests = " + consentRequests);
        if(consentRequests.size() > 0) {
            return new ResponseEntity<>(consentRequests, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<SentConsentRequest>(), HttpStatus.NOT_FOUND);
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

    @PostMapping("/get-consent-patient-request-otp")
    public ResponseEntity<GenericMessage> getConsentRequestOTP(@RequestBody RequestConsentObject requestConsentObject) {
        requestConsentObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
        otpService.getConsentRequestPatientOTP(requestConsentObject);
        return new ResponseEntity<>(new GenericMessage("OTP sent",
                "OTP sent on the patient's registered mobile number."),
                HttpStatus.OK);
    }

    @PostMapping("/get-consent-guardian-request-otp")
    public ResponseEntity<GenericMessage> getConsentRequestGuardianOTP(@RequestBody RequestConsentObject requestConsentObject) {
        requestConsentObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
        otpService.getConsentRequestGuardianOTP(requestConsentObject);
        return new ResponseEntity<>(new GenericMessage("OTP sent",
                "OTP sent on the guardian's registered mobile number."),
                HttpStatus.OK);
    }

    @PostMapping("/verify-consent-request-otp")
    public ResponseEntity<GenericMessage> verifyConsentRequestOTP(@RequestBody VerifyOTP verifyOTP) {
        Boolean result = otpService.verifyConsentRequestOTP(verifyOTP);
        if(result) {
            return new ResponseEntity<>(new GenericMessage("OTP verified",
                    "Consent is approved. You can check it in the approved consents section. You can proceed to requesting data."),
                    HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new GenericMessage("OTP not verified",
                    "OTP is not verified. Please try again."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/request-emergency-data")
    public ResponseEntity<GenericMessage> requestEmergencyData(@RequestBody RequestDataObject requestDataObject) {
        requestDataObject.setRecordRequesterHospital(MessagingConfig.hospitalId);
        consentService.requestEmergencyData(requestDataObject);
        return new ResponseEntity<>(new GenericMessage("Emergency request forwarded",
                "Data will arrive soon."),
                HttpStatus.OK);
    }

    @GetMapping("/get-received-records")
    public List<ReceivedRecords> getReceivedRecords() {
        List<ReceivedRecords> patientRecordList = recordServices.getReceivedRecords();
        return patientRecordList;
    }

    @GetMapping("/get-patient-records")
    public List<PatientRecord> getPatientRecords() {
        List<PatientRecord> patientRecordList = recordServices.getPatientRecords();
        return patientRecordList;
    }

    @GetMapping("/clear-records")
    public ResponseEntity<GenericMessage> clearRecords() {
        recordServices.clearRecords();
        return new ResponseEntity<>(new GenericMessage("Records cleared",
                "Records Cleared Successfully."),
                HttpStatus.OK);
    }

    @GetMapping("/get-pending-records")
    public List<PatientRecord> getPendingRecords() {
        List<PatientRecord> patientRecordList = recordServices.getPendingRecords();
        return patientRecordList;
    }


}
