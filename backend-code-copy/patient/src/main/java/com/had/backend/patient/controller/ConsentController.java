package com.had.backend.patient.controller;

import com.had.backend.patient.entity.ConsentRequest;
import com.had.backend.patient.entity.PatientRecord;
import com.had.backend.patient.model.ConsentMessage;
import com.had.backend.patient.model.GenericMessage;
import com.had.backend.patient.model.RequestDataObject;
import com.had.backend.patient.service.ConsentService;
import com.had.backend.patient.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consent")
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private DataService dataService;

    @PostMapping("/sendConsent")
    public ResponseEntity<String> sendConsent(@RequestBody ConsentMessage request) {
        consentService.sendConsent(request);
        return new ResponseEntity<>("Consent Sent Successfully", HttpStatus.OK);
    }

    @GetMapping("/getConsents")
    public List<ConsentRequest> getConsents() {
        List<ConsentRequest> consentRequests = consentService.getConsents();
        return consentRequests;
    }

    @GetMapping("/getApprovedConsents")
    public List<ConsentRequest> getApprovedConsents() {
        List<ConsentRequest> consentRequests = consentService.getApprovedConsents();
        return consentRequests;
    }

    @GetMapping("/getPendingConsents")
    public List<ConsentRequest> getPendingConsents() {
        System.out.println("Reached");
        List<ConsentRequest> consentRequests = consentService.getPendingConsents();
        return consentRequests;
    }

    @PostMapping("/getConsentsFromHospital")
    public List<ConsentRequest> getConsentsFromHospital(@RequestBody String hospitalId) {
        List<ConsentRequest> consentRequests = consentService.getConsentsFromHospital(hospitalId);
        return consentRequests;
    }

    @PostMapping("/get-records-from-hospital")
    public ResponseEntity<GenericMessage> fetchRecordsFromHospital(@RequestBody RequestDataObject requestDataObject) {
        requestDataObject.setDoctorId("NA");
        System.out.println("requestDataObject = " + requestDataObject);
        Boolean response = dataService.requestData(requestDataObject);
        if(response) {
            return new ResponseEntity<>(new GenericMessage("Get Patient Records Requested By Patient",
                    "Data will arrive soon."),
                    HttpStatus.OK);

        }
        return new ResponseEntity<>(new GenericMessage("Error in pushing Patient Data Request Message",
                "Please Inspect Logs."),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getRecords")
    public List<PatientRecord> getRecords() {
        List<PatientRecord> patientRecordList = consentService.getRecords();
        return patientRecordList;
    }

    @GetMapping("/clear-records")
    public ResponseEntity<GenericMessage> clearRecords() {
        consentService.clearRecords();
        return new ResponseEntity<>(new GenericMessage("Records cleared",
                "Records Cleared Successfully."),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete-record/{id}")
    public ResponseEntity<GenericMessage> deleteRecord(@PathVariable String id) {
        System.out.println("Running Delete Consent Record");
        Long Id = Long.parseLong(id);
        Boolean response = consentService.deleteRecord(Id);
        if(response) {
            return new ResponseEntity<>(new GenericMessage("Consent Record Deleted Successfully",
                    "Operation Success."),
                    HttpStatus.OK);

        }
        return new ResponseEntity<>(new GenericMessage("Consent Record with ID not found",
                "Please Inspect Logs."),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/revoke-consent/{id}")
    public ResponseEntity<GenericMessage> revokeConsent(@PathVariable String id) {
        consentService.revokeConsent(id);
        return new ResponseEntity<>(new GenericMessage("Consent will be revoked.", ""), HttpStatus.OK);
    }
}
