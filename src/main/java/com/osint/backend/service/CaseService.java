package com.osint.backend.service;

import com.osint.backend.model.AuditLog;
import com.osint.backend.model.CaseRecord;
import com.osint.backend.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final AuditLogService auditLogService;

    public CaseService(CaseRepository caseRepository,
                       AuditLogService auditLogService) {
        this.caseRepository = caseRepository;
        this.auditLogService = auditLogService;
    }

    public CaseRecord createCase(CaseRecord caseRecord, String actor) {
        caseRecord.setCreatedAt(LocalDateTime.now());

        if (caseRecord.getStatus() == null || caseRecord.getStatus().isBlank()) {
            caseRecord.setStatus("OPEN");
        }

        CaseRecord saved = caseRepository.save(caseRecord);

        auditLogService.log(
                actor,
                AuditLog.Action.CREATE,
                "CASE",
                saved.getId(),
                "Created case: " + safe(saved.getTitle())
        );

        return saved;
    }

    public List<CaseRecord> getAllCases() {
        return caseRepository.findAll();
    }

    public CaseRecord getCaseById(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }

    public CaseRecord updateCase(Long id, CaseRecord updatedCase, String actor) {
        CaseRecord existingCase = getCaseById(id);

        existingCase.setTitle(updatedCase.getTitle());
        existingCase.setDescription(updatedCase.getDescription());
        existingCase.setStatus(updatedCase.getStatus());

        CaseRecord saved = caseRepository.save(existingCase);

        auditLogService.log(
                actor,
                AuditLog.Action.UPDATE,
                "CASE",
                saved.getId(),
                "Updated case: " + safe(saved.getTitle())
        );

        return saved;
    }

    public void deleteCase(Long id, String actor) {
        CaseRecord existingCase = getCaseById(id);

        caseRepository.deleteById(id);

        auditLogService.log(
                actor,
                AuditLog.Action.DELETE,
                "CASE",
                id,
                "Deleted case: " + safe(existingCase.getTitle())
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}