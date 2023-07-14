package com.demographic.demographic.entity;

import com.demographic.demographic.config.AesEncryptor;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Convert(converter = AesEncryptor.class)
    @Column(unique = true)
    private String aadhar;
//    @Convert(converter = AesEncryptor.class)
    private String firstName;
//    @Convert(converter = AesEncryptor.class)
    private String lastName;
//    @Convert(converter = AesEncryptor.class)
    private String phoneNumber;
    private String godFatherName;
    private String godFatherNumber;

//    @Convert(converter = AesEncryptor.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDateTime dateOfBirth;

//    @Convert(converter = AesEncryptor.class)
    @Setter(AccessLevel.NONE) private Integer age;
    public void setAge() {
        this.age = Math.toIntExact(LocalDateTime.from(this.dateOfBirth).until(LocalDateTime.now(), ChronoUnit.YEARS ));
    }
}
