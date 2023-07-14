package com.demographic.demographic.repository;

import com.demographic.demographic.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Boolean existsHospitalByHospitalName(String hospitalName);
    List<Hospital> findAll();
}
