package com.osint.backend.controller;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.service.PersonRecordService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person-records")
@CrossOrigin(origins = "http://localhost:5173")
public class PersonRecordController {

    private final PersonRecordService personRecordService;

    public PersonRecordController(PersonRecordService personRecordService) {
        this.personRecordService = personRecordService;
    }

    @PostMapping
    public PersonRecord createRecord(@RequestBody PersonRecord record,
                                     Authentication auth) {
        return personRecordService.createRecord(record, actor(auth));
    }

    @GetMapping
    public List<PersonRecord> getAllRecords() {
        return personRecordService.getAllRecords();
    }

    @GetMapping("/{id}")
    public PersonRecord getRecordById(@PathVariable Long id) {
        return personRecordService.getRecordById(id);
    }

    @GetMapping("/case/{caseId}")
    public List<PersonRecord> getRecordsByCaseId(@PathVariable Long caseId) {
        return personRecordService.getRecordsByCaseId(caseId);
    }

    @GetMapping("/search")
    public List<PersonRecord> search(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) String query) {
        if (query != null && !query.isBlank()) {
            return personRecordService.globalSearch(query);
        }
        return personRecordService.searchByFullName(name == null ? "" : name);
    }

    @GetMapping("/global-search")
    public List<PersonRecord> globalSearch(@RequestParam String query) {
        return personRecordService.globalSearch(query);
    }

    @PutMapping("/{id}")
    public PersonRecord updateRecord(@PathVariable Long id,
                                     @RequestBody PersonRecord record,
                                     Authentication auth) {
        return personRecordService.updateRecord(id, record, actor(auth));
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id, Authentication auth) {
        personRecordService.deleteRecord(id, actor(auth));
        return "Person record deleted successfully";
    }

    private String actor(Authentication auth) {
        return (auth != null) ? auth.getName() : "unknown";
    }
}