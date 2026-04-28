package com.osint.backend.service;

import com.osint.backend.model.SourceRecord;
import com.osint.backend.repository.SourceRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SourceRecordService {

    private final SourceRecordRepository sourceRecordRepository;

    public SourceRecordService(SourceRecordRepository sourceRecordRepository) {
        this.sourceRecordRepository = sourceRecordRepository;
    }

    public SourceRecord createSource(SourceRecord sourceRecord) {
        sourceRecord.setCreatedAt(LocalDateTime.now());

        if (sourceRecord.getConfidenceScore() == null) {
            sourceRecord.setConfidenceScore(0.5);
        }

        return sourceRecordRepository.save(sourceRecord);
    }

    public List<SourceRecord> getAllSources() {
        return sourceRecordRepository.findAll();
    }

    public SourceRecord getSourceById(Long id) {
        return sourceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Source record not found"));
    }

    public List<SourceRecord> getSourcesByPersonRecordId(Long personRecordId) {
        return sourceRecordRepository.findByPersonRecordId(personRecordId);
    }

    public List<SourceRecord> searchByPlatform(String platform) {
        return sourceRecordRepository.findByPlatformContainingIgnoreCase(platform);
    }

    public SourceRecord updateSource(Long id, SourceRecord updatedSource) {
        SourceRecord existingSource = sourceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Source record not found"));

        existingSource.setPersonRecordId(updatedSource.getPersonRecordId());
        existingSource.setPlatform(updatedSource.getPlatform());
        existingSource.setUsername(updatedSource.getUsername());
        existingSource.setSourceUrl(updatedSource.getSourceUrl());
        existingSource.setCapturedText(updatedSource.getCapturedText());
        existingSource.setConfidenceScore(updatedSource.getConfidenceScore());

        return sourceRecordRepository.save(existingSource);
    }

    public void deleteSource(Long id) {
        sourceRecordRepository.deleteById(id);
    }
}