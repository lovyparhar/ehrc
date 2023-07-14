package com.demographic.demographic.controller;

import com.demographic.demographic.entity.Hospital;
import com.demographic.demographic.model.AddHospitalRequest;
import com.demographic.demographic.model.GenericMessage;
import com.demographic.demographic.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospital-demographic")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/get-hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitalList = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitalList);
    }

    @PostMapping("/add-hospital")
    public ResponseEntity<GenericMessage> addHospital(@RequestBody AddHospitalRequest addHospitalRequest) {
        //System.out.println(addHospitalRequest.toString());
        boolean response  = hospitalService.addHospital(addHospitalRequest);
        if(response){
            return new ResponseEntity<>(new GenericMessage("Add Hospital",
                    "Successfully"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericMessage("Add Hospital",
                "Failed"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
