package com.osint.backend.controller;

import com.osint.backend.dto.DashboardStatsResponse;
import com.osint.backend.repository.CaseRepository;
import com.osint.backend.repository.PersonRecordRepository;
import com.osint.backend.repository.SourceRecordRepository;
import com.osint.backend.repository.UserAccountRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final CaseRepository caseRepository;
    private final PersonRecordRepository personRecordRepository;
    private final SourceRecordRepository sourceRecordRepository;
    private final UserAccountRepository userAccountRepository;

    public DashboardController(CaseRepository caseRepository,
                               PersonRecordRepository personRecordRepository,
                               SourceRecordRepository sourceRecordRepository,
                               UserAccountRepository userAccountRepository) {
        this.caseRepository = caseRepository;
        this.personRecordRepository = personRecordRepository;
        this.sourceRecordRepository = sourceRecordRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        return new DashboardStatsResponse(
                caseRepository.count(),
                caseRepository.countByStatusIgnoreCase("OPEN"),
                caseRepository.countByStatusIgnoreCase("IN_PROGRESS"),
                caseRepository.countByStatusIgnoreCase("CLOSED"),
                personRecordRepository.count(),
                sourceRecordRepository.count(),
                userAccountRepository.count()
        );
    }
}