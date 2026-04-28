package com.osint.backend.repository;

import com.osint.backend.model.PersonRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {
    List<PersonRecord> findByCaseId(Long caseId);
    List<PersonRecord> findByFullNameContainingIgnoreCase(String fullName);
}