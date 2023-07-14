package com.had.backend.hospital.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceivedConsentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // The id of the consent record in source
    // The current hospital will be source in this case
    private Long idSC;

    // The id of the consent record in consent manager app
    private Long idCM;

    // The id of the consent record in patient app
    private Long idPC;

    // The id of the consent record in destination
    private Long idDC;

    // Whether the consent is approved or not
    private boolean approved;

    // The id of the patient whose consent is asked for
    private String patientId;
    private String department;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    // The id/queue name of the source hospital
    private String sourceHospitalId;

    // The id/queue name of the destination hospital
    private String destinationHospitalId;

    public Long getId() {
        return idPC;
    }

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
