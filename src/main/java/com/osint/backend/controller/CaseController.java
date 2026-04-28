package com.osint.backend.controller;

import com.osint.backend.model.CaseRecord;
import com.osint.backend.service.CaseService;
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
    public CaseRecord createCase(@RequestBody CaseRecord caseRecord) {
        return caseService.createCase(caseRecord);
    }

    @PutMapping("/{id}")
    public CaseRecord updateCase(@PathVariable Long id,
                                 @RequestBody CaseRecord updatedCase) {
        return caseService.updateCase(id, updatedCase);
    }

    @DeleteMapping("/{id}")
    public String deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return "Case deleted successfully";
    }
}