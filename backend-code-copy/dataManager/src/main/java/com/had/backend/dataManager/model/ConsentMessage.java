package com.had.backend.dataManager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsentMessage {

    private Long idPC;
    private Long idCM;
    private Long idSC;
    private Long idDC;
    private boolean approved;
    private String patientId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
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
