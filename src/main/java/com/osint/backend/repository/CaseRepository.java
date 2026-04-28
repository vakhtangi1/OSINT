package com.osint.backend.repository;

import com.osint.backend.model.CaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<CaseRecord, Long> {

    long countByStatusIgnoreCase(String status);
}