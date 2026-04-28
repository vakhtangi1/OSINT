package com.osint.backend.repository;

import com.osint.backend.model.SourceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceRecordRepository extends JpaRepository<SourceRecord, Long> {
    List<SourceRecord> findByPersonRecordId(Long personRecordId);
    List<SourceRecord> findByPlatformContainingIgnoreCase(String platform);
}