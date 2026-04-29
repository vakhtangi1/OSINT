package com.osint.backend.controller;

import com.osint.backend.model.AuditLog;
import com.osint.backend.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public Page<AuditLog> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String action
    ) {
        if (actor != null && !actor.isBlank()) {
            return auditLogService.getByActor(actor, page, size);
        }

        if (action != null && !action.isBlank()) {
            return auditLogService.getByAction(
                    AuditLog.Action.valueOf(action.toUpperCase()),
                    page,
                    size
            );
        }

        return auditLogService.getAll(page, size);
    }
}