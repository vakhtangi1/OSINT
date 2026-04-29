package com.osint.backend.service;

import com.osint.backend.model.AuditLog;
import com.osint.backend.model.PersonRecord;
import com.osint.backend.repository.PersonRecordRepository;
import com.osint.backend.security.CryptoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PersonRecordService {

    private final PersonRecordRepository personRecordRepository;
    private final AuditLogService auditLogService;

    public PersonRecordService(PersonRecordRepository personRecordRepository,
                               AuditLogService auditLogService) {
        this.personRecordRepository = personRecordRepository;
        this.auditLogService = auditLogService;
    }

    public PersonRecord createRecord(PersonRecord record, String actor) {
        record.setCreatedAt(LocalDateTime.now());

        if (record.getConfidenceScore() == null) {
            record.setConfidenceScore(0.5);
        }

        prepareSearchAndHashes(record);

        PersonRecord saved = personRecordRepository.save(record);

        auditLogService.log(
                actor,
                AuditLog.Action.CREATE,
                "PERSON_RECORD",
                saved.getId(),
                "Created person record: " + safeName(saved)
        );

        return saved;
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

    public List<PersonRecord> searchByFullName(String name, String actor) {
        auditLogService.log(
                actor,
                AuditLog.Action.SEARCH,
                "PERSON_RECORD",
                null,
                "Searched person by name: " + safe(name)
        );

        return searchByFullName(name);
    }

    public List<PersonRecord> globalSearch(String query) {
        if (query == null || query.isBlank()) {
            return personRecordRepository.findAll();
        }

        String cleanQuery = query.trim().toLowerCase();

        String emailHash = CryptoUtil.hmacHash(cleanQuery);
        String phoneHash = CryptoUtil.hmacHash(cleanQuery);

        List<PersonRecord> hashResults =
                personRecordRepository.findByEmailHashOrPhoneHash(emailHash, phoneHash);

        if (!hashResults.isEmpty()) {
            return hashResults;
        }

        return personRecordRepository.searchBySearchText(cleanQuery);
    }

    public List<PersonRecord> globalSearch(String query, String actor) {
        auditLogService.log(
                actor,
                AuditLog.Action.SEARCH,
                "PERSON_RECORD",
                null,
                "Global search query used"
        );

        return globalSearch(query);
    }

    public PersonRecord updateRecord(Long id, PersonRecord updatedRecord, String actor) {
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

        PersonRecord saved = personRecordRepository.save(existing);

        auditLogService.log(
                actor,
                AuditLog.Action.UPDATE,
                "PERSON_RECORD",
                saved.getId(),
                "Updated person record: " + safeName(saved)
        );

        return saved;
    }

    public void deleteRecord(Long id, String actor) {
        PersonRecord existing = getRecordById(id);

        personRecordRepository.deleteById(id);

        auditLogService.log(
                actor,
                AuditLog.Action.DELETE,
                "PERSON_RECORD",
                id,
                "Deleted person record: " + safeName(existing)
        );
    }

    private void prepareSearchAndHashes(PersonRecord record) {
        String email = clean(record.getEmail());
        String phone = clean(record.getPhoneNumber());

        record.setEmailHash(CryptoUtil.hmacHash(email));
        record.setPhoneHash(CryptoUtil.hmacHash(phone));

        String searchText = String.join(" ",
                safe(record.getFirstName()),
                safe(record.getLastName()),
                safe(record.getFullName()),
                safe(record.getCompany()),
                safe(record.getJobTitle()),
                safe(record.getLocation()),
                safe(record.getSourceType())
        ).toLowerCase();

        record.setSearchText(searchText);
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeName(PersonRecord record) {
        if (record.getFullName() != null && !record.getFullName().isBlank()) {
            return record.getFullName();
        }

        return (safe(record.getFirstName()) + " " + safe(record.getLastName())).trim();
    }
}