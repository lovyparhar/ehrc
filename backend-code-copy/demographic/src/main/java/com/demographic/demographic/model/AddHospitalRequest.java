package com.demographic.demographic.model;

import com.demographic.demographic.entity.Hospital;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddHospitalRequest {
    private String hospitalName;
    private String address;
    private String departments;
}
