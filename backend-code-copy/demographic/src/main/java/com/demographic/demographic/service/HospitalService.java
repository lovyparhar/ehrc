package com.demographic.demographic.service;

import com.demographic.demographic.entity.Hospital;
import com.demographic.demographic.model.AddHospitalRequest;
import com.demographic.demographic.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Boolean addHospital(AddHospitalRequest addHospitalRequest) {
        Hospital hospital = Hospital.builder()
                .hospitalName(addHospitalRequest.getHospitalName())
                .address(addHospitalRequest.getAddress())
                .departments(addHospitalRequest.getDepartments())
                .build();

        //System.out.println(addHospitalRequest.toString());
        hospitalRepository.saveAndFlush(hospital);

        return true;
    }


}
