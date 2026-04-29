package com.osint.backend.controller;

import com.osint.backend.model.CaseRecord;
import com.osint.backend.service.CaseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "http://localhost:5173")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public List<CaseRecord> getAllCases() {
        return caseService.getAllCases();
    }

    @GetMapping("/{id}")
    public CaseRecord getCaseById(@PathVariable Long id) {
        return caseService.getCaseById(id);
    }

    @PostMapping
    public CaseRecord createCase(@RequestBody CaseRecord caseRecord,
                                 Authentication auth) {
        return caseService.createCase(caseRecord, actor(auth));
    }

    @PutMapping("/{id}")
    public CaseRecord updateCase(@PathVariable Long id,
                                 @RequestBody CaseRecord updatedCase,
                                 Authentication auth) {
        return caseService.updateCase(id, updatedCase, actor(auth));
    }

    @DeleteMapping("/{id}")
    public String deleteCase(@PathVariable Long id,
                             Authentication auth) {
        caseService.deleteCase(id, actor(auth));
        return "Case deleted successfully";
    }

    private String actor(Authentication auth) {
        return auth != null ? auth.getName() : "unknown";
    }
}