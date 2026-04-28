package com.osint.backend.controller;

import com.osint.backend.model.SourceRecord;
import com.osint.backend.service.SourceRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/source-records")
public class SourceRecordController {

    private final SourceRecordService sourceRecordService;

    public SourceRecordController(SourceRecordService sourceRecordService) {
        this.sourceRecordService = sourceRecordService;
    }

    @PostMapping
    public SourceRecord createSource(@RequestBody SourceRecord sourceRecord) {
        return sourceRecordService.createSource(sourceRecord);
    }

    @GetMapping
    public List<SourceRecord> getAllSources() {
        return sourceRecordService.getAllSources();
    }

    @GetMapping("/{id}")
    public SourceRecord getSourceById(@PathVariable Long id) {
        return sourceRecordService.getSourceById(id);
    }

    @GetMapping("/person/{personRecordId}")
    public List<SourceRecord> getSourcesByPersonRecordId(@PathVariable Long personRecordId) {
        return sourceRecordService.getSourcesByPersonRecordId(personRecordId);
    }

    @GetMapping("/search")
    public List<SourceRecord> searchByPlatform(@RequestParam String platform) {
        return sourceRecordService.searchByPlatform(platform);
    }

    @PutMapping("/{id}")
    public SourceRecord updateSource(@PathVariable Long id, @RequestBody SourceRecord sourceRecord) {
        return sourceRecordService.updateSource(id, sourceRecord);
    }

    @DeleteMapping("/{id}")
    public String deleteSource(@PathVariable Long id) {
        sourceRecordService.deleteSource(id);
        return "Source record deleted successfully";
    }
}