package com.osint.backend.service;

import com.osint.backend.model.AuditLog;
import com.osint.backend.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String actor, AuditLog.Action action,
                    String entityType, Long entityId, String detail) {
        auditLogRepository.save(new AuditLog(actor, action, entityType, entityId, detail));
    }

    public void log(String actor, AuditLog.Action action, String detail) {
        log(actor, action, "-", null, detail);
    }

    public Page<AuditLog> getAll(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public Page<AuditLog> getByActor(String actor, int page, int size) {
        return auditLogRepository.findByActorOrderByTimestampDesc(actor, PageRequest.of(page, size));
    }
}