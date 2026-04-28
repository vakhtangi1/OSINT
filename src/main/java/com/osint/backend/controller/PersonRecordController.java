package com.osint.backend.controller;

import com.osint.backend.model.PersonRecord;
import com.osint.backend.service.PersonRecordService;
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
    public PersonRecord createRecord(@RequestBody PersonRecord record) {
        return personRecordService.createRecord(record);
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
    public List<PersonRecord> searchByFullName(@RequestParam String name) {
        return personRecordService.searchByFullName(name);
    }

    @PutMapping("/{id}")
    public PersonRecord updateRecord(@PathVariable Long id, @RequestBody PersonRecord record) {
        return personRecordService.updateRecord(id, record);
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id) {
        personRecordService.deleteRecord(id);
        return "Person record deleted successfully";
    }
}