package com.osint.backend.service;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.repository.PersonRecordRepository;
import com.osint.backend.security.CryptoUtil;
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
            String firstName = record.getFirstName() == null ? "" : record.getFirstName();
            String lastName = record.getLastName() == null ? "" : record.getLastName();
            record.setFullName((firstName + " " + lastName).trim());
        }

        prepareSearchAndHashes(record);

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

    public List<PersonRecord> globalSearch(String query) {
        String safeQuery = query == null ? "" : query.trim().toLowerCase();

        String emailHash = CryptoUtil.hmacHash(normalizeEmail(safeQuery));
        String phoneHash = CryptoUtil.hmacHash(normalizePhone(safeQuery));

        return personRecordRepository.globalSearch(safeQuery, emailHash, phoneHash);
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

        prepareSearchAndHashes(existing);

        return personRecordRepository.save(existing);
    }

    public void deleteRecord(Long id) {
        personRecordRepository.deleteById(id);
    }

    private void prepareSearchAndHashes(PersonRecord record) {
        record.setSearchText(buildSearchText(record));
        record.setEmailHash(CryptoUtil.hmacHash(normalizeEmail(record.getEmail())));
        record.setPhoneHash(CryptoUtil.hmacHash(normalizePhone(record.getPhoneNumber())));
    }

    private String buildSearchText(PersonRecord record) {
        return String.join(" ",
                safe(record.getFirstName()),
                safe(record.getLastName()),
                safe(record.getFullName()),
                safe(record.getJobTitle()),
                safe(record.getCompany()),
                safe(record.getLocation()),
                safe(record.getSourceType())
        ).toLowerCase();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.replaceAll("[^0-9+]", "");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}