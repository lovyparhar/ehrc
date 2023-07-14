package com.had.backend.hospital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDataObject {
    private String recordSenderHospital; // we need to get records from this hospital to us!
    private String recordRequesterHospital; // we need to get records to this hospital from the sender!
    private String patientId;
    private String department;

    private String doctorId;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestTime;
}
