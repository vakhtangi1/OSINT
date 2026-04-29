package com.osint.backend.controller;

import com.osint.backend.model.AuditLog;
import com.osint.backend.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /** GET /api/admin/audit-logs?page=0&size=50 */
    @GetMapping
    public Page<AuditLog> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {
        return auditLogService.getAll(page, size);
    }

    /** GET /api/admin/audit-logs/user/{username}?page=0&size=50 */
    @GetMapping("/user/{username}")
    public Page<AuditLog> getByActor(
            @PathVariable String username,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {
        return auditLogService.getByActor(username, page, size);
    }
}