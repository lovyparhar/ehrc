package com.had.backend.hospital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentMessage {

    private Long idPC;
    private Long idCM;
    private Long idSC;
    private Long idDC;
    private boolean approved;
    private String patientId;
    private String department;


    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private String sourceHospitalId;
    private String destinationHospitalId;
    public String getPatientId() {
        return patientId;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getSourceHospitalId() {
        return sourceHospitalId;
    }

    public String getDestinationHospitalId() {
        return destinationHospitalId;
    }
}
