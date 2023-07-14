package com.had.backend.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDemographicResponse {
    private String aadhar;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String godFatherName;
    private String godFatherNumber;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateOfBirth;
    private Integer age;
    private String message;
}
