package com.osint.backend.service;

import com.osint.backend.model.CaseRecord;
import com.osint.backend.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public CaseRecord createCase(CaseRecord caseRecord) {
        caseRecord.setCreatedAt(LocalDateTime.now());
        caseRecord.setStatus("OPEN");
        return caseRepository.save(caseRecord);
    }

    public List<CaseRecord> getAllCases() {
        return caseRepository.findAll();
    }

    public CaseRecord getCaseById(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }

    public CaseRecord updateCase(Long id, CaseRecord updatedCase) {
        CaseRecord existingCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        existingCase.setTitle(updatedCase.getTitle());
        existingCase.setDescription(updatedCase.getDescription());
        existingCase.setStatus(updatedCase.getStatus());

        return caseRepository.save(existingCase);
    }

    public void deleteCase(Long id) {
        caseRepository.deleteById(id);
    }
}