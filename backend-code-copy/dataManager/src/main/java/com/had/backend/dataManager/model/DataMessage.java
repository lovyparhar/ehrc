package com.had.backend.dataManager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMessage {
    private String sourceId;
    private String destinationId;

    private String department;
    private String address;
    private String diagnosis;
    private String prescription;
    private String aadhar;
}
