package com.osint.backend.controller;

import com.osint.backend.model.AuditLog;
import com.osint.backend.model.CaseRecord;
import com.osint.backend.model.PersonRecord;
import com.osint.backend.model.SourceRecord;
import com.osint.backend.repository.CaseRepository;
import com.osint.backend.repository.PersonRecordRepository;
import com.osint.backend.repository.SourceRecordRepository;
import com.osint.backend.service.AuditLogService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final CaseRepository caseRepository;
    private final PersonRecordRepository personRecordRepository;
    private final SourceRecordRepository sourceRecordRepository;
    private final AuditLogService auditLogService;

    public ReportController(CaseRepository caseRepository,
                            PersonRecordRepository personRecordRepository,
                            SourceRecordRepository sourceRecordRepository,
                            AuditLogService auditLogService) {
        this.caseRepository = caseRepository;
        this.personRecordRepository = personRecordRepository;
        this.sourceRecordRepository = sourceRecordRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/case/{caseId}/pdf")
    public ResponseEntity<byte[]> exportCaseReport(@PathVariable Long caseId,
                                                   Authentication auth) {
        try {
            CaseRecord caseRecord = caseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found"));

            List<PersonRecord> persons = personRecordRepository.findByCaseId(caseId);

            List<SourceRecord> sources = new ArrayList<>();
            for (PersonRecord person : persons) {
                sources.addAll(sourceRecordRepository.findByPersonRecordId(person.getId()));
            }

            byte[] pdf = buildPdf(caseRecord, persons, sources);

            auditLogService.log(
                    actor(auth),
                    AuditLog.Action.EXPORT,
                    "CASE_REPORT",
                    caseId,
                    "Exported PDF report for case: " + safe(caseRecord.getTitle())
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=osint_case_" + caseId + "_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private byte[] buildPdf(CaseRecord caseRecord,
                            List<PersonRecord> persons,
                            List<SourceRecord> sources) throws Exception {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = 750;

            y = writeLine(content, PDType1Font.HELVETICA_BOLD, 20,
                    "OSINT Investigation Report", 50, y);

            y -= 15;

            y = writeLine(content, PDType1Font.HELVETICA_BOLD, 13,
                    "Case Information", 50, y);

            y = writeLine(content, PDType1Font.HELVETICA, 11,
                    "Case ID: " + caseRecord.getId(), 50, y);

            y = writeLine(content, PDType1Font.HELVETICA, 11,
                    "Title: " + safe(caseRecord.getTitle()), 50, y);

            y = writeLine(content, PDType1Font.HELVETICA, 11,
                    "Status: " + safe(caseRecord.getStatus()), 50, y);

            y = writeLine(content, PDType1Font.HELVETICA, 11,
                    "Description: " + safe(caseRecord.getDescription()), 50, y);

            y = writeLine(content, PDType1Font.HELVETICA, 11,
                    "Generated At: " + LocalDateTime.now(), 50, y);

            y -= 15;

            y = writeLine(content, PDType1Font.HELVETICA_BOLD, 13,
                    "Persons Connected To Case", 50, y);

            if (persons.isEmpty()) {
                y = writeLine(content, PDType1Font.HELVETICA, 11,
                        "No persons saved for this case.", 50, y);
            }

            for (PersonRecord p : persons) {
                if (y < 80) {
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = 750;
                }

                y = writeLine(content, PDType1Font.HELVETICA, 11,
                        "- " + safe(p.getFullName())
                                + " | " + safe(p.getJobTitle())
                                + " | " + safe(p.getCompany())
                                + " | " + safe(p.getLocation()),
                        50, y);
            }

            y -= 15;

            y = writeLine(content, PDType1Font.HELVETICA_BOLD, 13,
                    "Sources / Evidence", 50, y);

            if (sources.isEmpty()) {
                y = writeLine(content, PDType1Font.HELVETICA, 11,
                        "No sources saved for this case.", 50, y);
            }

            for (SourceRecord s : sources) {
                if (y < 80) {
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = 750;
                }

                y = writeLine(content, PDType1Font.HELVETICA, 11,
                        "- " + safe(s.getPlatform())
                                + " | Username: " + safe(s.getUsername())
                                + " | Confidence: " + s.getConfidenceScore(),
                        50, y);

                y = writeLine(content, PDType1Font.HELVETICA, 10,
                        "  URL: " + safe(s.getSourceUrl()),
                        60, y);
            }

            content.close();

            document.save(output);
            return output.toByteArray();
        }
    }

    private float writeLine(PDPageContentStream content,
                            PDType1Font font,
                            int size,
                            String text,
                            float x,
                            float y) throws Exception {

        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(cleanPdfText(text));
        content.endText();

        return y - 18;
    }

    private String actor(Authentication auth) {
        return auth != null ? auth.getName() : "unknown";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String cleanPdfText(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .replaceAll("[^\\x20-\\x7E]", "");
    }
}