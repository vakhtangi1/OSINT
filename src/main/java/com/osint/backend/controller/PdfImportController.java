package com.osint.backend.controller;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.service.PdfImportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf-import")
@CrossOrigin(origins = "http://localhost:5173")
public class PdfImportController {

    private final PdfImportService pdfImportService;

    public PdfImportController(PdfImportService pdfImportService) {
        this.pdfImportService = pdfImportService;
    }

    @PostMapping("/person/{caseId}")
    public PersonRecord importPersonFromPdf(
            @PathVariable Long caseId,
            @RequestParam("file") MultipartFile file
    ) {
        return pdfImportService.importPersonFromPdf(caseId, file);
    }
}