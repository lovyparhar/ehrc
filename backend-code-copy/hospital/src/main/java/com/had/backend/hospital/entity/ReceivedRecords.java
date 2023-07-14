package com.had.backend.hospital.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.had.backend.hospital.config.AesEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
public class ReceivedRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recievedDate;

    @Convert(converter = AesEncryptor.class)
    private String hospitalName;

    @Convert(converter = AesEncryptor.class)
    private String patientFirstName;

    @Convert(converter = AesEncryptor.class)
    private String patientLastName;

    @Convert(converter = AesEncryptor.class)
    private String department;

    @Convert(converter = AesEncryptor.class)
    private String address;

    @Convert(converter = AesEncryptor.class)
    private String diagnosis;

    @Convert(converter = AesEncryptor.class)
    private String prescription;

    @Convert(converter = AesEncryptor.class)
    private String aadhar;

    @Convert(converter = AesEncryptor.class)
    private String doctorId;
}
