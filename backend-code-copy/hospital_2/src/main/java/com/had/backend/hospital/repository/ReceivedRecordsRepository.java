package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.entity.ReceivedRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivedRecordsRepository extends JpaRepository<ReceivedRecords, Long> {

}
