package com.osint.backend.controller;

import com.osint.backend.dto.DorkingResponse;
import com.osint.backend.service.DorkingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dorking")
@CrossOrigin(origins = "http://localhost:5173")
public class DorkingController {

    private final DorkingService dorkingService;

    public DorkingController(DorkingService dorkingService) {
        this.dorkingService = dorkingService;
    }

    @GetMapping
    public DorkingResponse generateQueries(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "general") String type
    ) {
        return dorkingService.generateQueries(keyword, type);
    }
}