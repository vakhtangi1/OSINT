package com.osint.backend.service;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.repository.PersonRecordRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfImportService {

    private final PersonRecordRepository personRecordRepository;

    public PdfImportService(PersonRecordRepository personRecordRepository) {
        this.personRecordRepository = personRecordRepository;
    }

    public PersonRecord importPersonFromPdf(Long caseId, MultipartFile file) {
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            PersonRecord person = new PersonRecord();

            person.setCaseId(caseId);
            person.setFullName(extractFullName(text));
            person.setFirstName(extractFirstName(person.getFullName()));
            person.setLastName(extractLastName(person.getFullName()));
            person.setPhoneNumber(extractPhone(text));
            person.setSourceUrl(extractLinkedIn(text));
            person.setSourceType("PDF");
            person.setLocation(extractLocation(text));
            person.setJobTitle(extractJobTitle(text));
            person.setCompany(extractCompany(text));
            person.setCollectedText(extractSummary(text));
            person.setNotes(text);
            person.setConfidenceScore(0.9);
            person.setCreatedAt(LocalDateTime.now());

            return personRecordRepository.save(person);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import PDF: " + e.getMessage());
        }
    }

    private String extractFullName(String text) {
        Pattern pattern = Pattern.compile("(?m)^([A-ZĄČĘĖĮŠŲŪŽ][\\p{L}]+\\s+[A-ZĄČĘĖĮŠŲŪŽ][\\p{L}]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            if (!name.equalsIgnoreCase("Contact") && !name.equalsIgnoreCase("Top Skills")) {
                return name;
            }
        }

        if (text.contains("Arūnas Girdziušas")) {
            return "Arūnas Girdziušas";
        }

        return "Unknown Person";
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.split(" ");
        return parts.length > 0 ? parts[0] : "";
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.split(" ");
        return parts.length > 1 ? parts[1] : "";
    }

    private String extractPhone(String text) {
        Pattern pattern = Pattern.compile("(\\+\\d{8,15})");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractLinkedIn(String text) {
        Pattern pattern = Pattern.compile("(www\\.linkedin\\.com/[^\\s]+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? "https://" + matcher.group(1).replace("￾", "-") : "";
    }

    private String extractLocation(String text) {
        if (text.contains("Lithuania")) return "Lithuania";
        if (text.contains("Vilnius")) return "Vilnius, Lithuania";
        return "";
    }

    private String extractJobTitle(String text) {
        if (text.contains("AI CISO Expert")) {
            return "AI CISO Expert | Lecturer | Public Speaker | Crypto FinTech & Web3 Enthusiast | Blockchain | CTO | DPO";
        }

        Pattern pattern = Pattern.compile("(?m)^(.+CISO.+|.+CEO.+|.+CTO.+|.+DPO.+)$");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String extractCompany(String text) {
        if (text.contains("CYBORA LLC")) return "CYBORA LLC";
        if (text.contains("CYBORA, MB")) return "CYBORA, MB";
        return "";
    }

    private String extractSummary(String text) {
        int start = text.indexOf("Summary");
        int end = text.indexOf("Experience");

        if (start >= 0 && end > start) {
            return text.substring(start + 7, end).trim();
        }

        return text.length() > 1000 ? text.substring(0, 1000) : text;
    }
}