package com.osint.backend.service;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.repository.PersonRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PersonRecordService {

    private final PersonRecordRepository personRecordRepository;

    public PersonRecordService(PersonRecordRepository personRecordRepository) {
        this.personRecordRepository = personRecordRepository;
    }

    public PersonRecord createRecord(PersonRecord record) {
        record.setCreatedAt(LocalDateTime.now());

        if (record.getConfidenceScore() == null) {
            record.setConfidenceScore(0.5);
        }

        if (record.getFullName() == null || record.getFullName().isBlank()) {
            record.setFullName((record.getFirstName() + " " + record.getLastName()).trim());
        }

        return personRecordRepository.save(record);
    }

    public List<PersonRecord> getAllRecords() {
        return personRecordRepository.findAll();
    }

    public PersonRecord getRecordById(Long id) {
        return personRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person record not found"));
    }

    public List<PersonRecord> getRecordsByCaseId(Long caseId) {
        return personRecordRepository.findByCaseId(caseId);
    }

    public List<PersonRecord> searchByFullName(String name) {
        return personRecordRepository.findByFullNameContainingIgnoreCase(name);
    }

    public PersonRecord updateRecord(Long id, PersonRecord updatedRecord) {
        PersonRecord existing = getRecordById(id);

        existing.setCaseId(updatedRecord.getCaseId());
        existing.setFirstName(updatedRecord.getFirstName());
        existing.setLastName(updatedRecord.getLastName());
        existing.setFullName(updatedRecord.getFullName());
        existing.setJobTitle(updatedRecord.getJobTitle());
        existing.setCompany(updatedRecord.getCompany());
        existing.setLocation(updatedRecord.getLocation());
        existing.setEmail(updatedRecord.getEmail());
        existing.setPhoneNumber(updatedRecord.getPhoneNumber());
        existing.setProfilePhotoUrl(updatedRecord.getProfilePhotoUrl());
        existing.setSourceUrl(updatedRecord.getSourceUrl());
        existing.setSourceType(updatedRecord.getSourceType());
        existing.setCollectedText(updatedRecord.getCollectedText());
        existing.setNotes(updatedRecord.getNotes());
        existing.setConfidenceScore(updatedRecord.getConfidenceScore());

        return personRecordRepository.save(existing);
    }

    public void deleteRecord(Long id) {
        personRecordRepository.deleteById(id);
    }
}