package com.had.backend.hospital.entity;

import com.had.backend.hospital.config.AesEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Convert(converter = AesEncryptor.class)
    private String hospitalName;

//    @Convert(converter = AesEncryptor.class)
    private String patientFirstName;

//    @Convert(converter = AesEncryptor.class)
    private String patientLastName;

//    @Convert(converter = AesEncryptor.class)
    private String department;

//    @Convert(converter = AesEncryptor.class)
    private String address;

//    @Convert(converter = AesEncryptor.class)
    private String diagnosis;

//    @Convert(converter = AesEncryptor.class)
    private String prescription;

//    @Convert(converter = AesEncryptor.class)
    private String aadhar;

//    @Convert(converter = AesEncryptor.class)
    private String doctorId;

}
