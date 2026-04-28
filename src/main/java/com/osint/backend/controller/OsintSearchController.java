package com.osint.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/osint-search")
@CrossOrigin(origins = "http://localhost:5173")
public class OsintSearchController {

    @GetMapping
    public Map<String, String> buildSearchLinks(@RequestParam String query) {
        String q = query == null ? "" : query.trim();
        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);

        Map<String, String> links = new LinkedHashMap<>();

        links.put("Google General", "https://www.google.com/search?q=" + encoded);
        links.put("Google Exact Name", "https://www.google.com/search?q=%22" + encoded + "%22");
        links.put("LinkedIn", "https://www.google.com/search?q=site%3Alinkedin.com%2Fin+%22" + encoded + "%22");
        links.put("GitHub", "https://www.google.com/search?q=site%3Agithub.com+%22" + encoded + "%22");
        links.put("PDF Documents", "https://www.google.com/search?q=%22" + encoded + "%22+filetype%3Apdf");
        links.put("DOCX Documents", "https://www.google.com/search?q=%22" + encoded + "%22+filetype%3Adocx");
        links.put("Email Exposure", "https://www.google.com/search?q=%22" + encoded + "%22+email");
        links.put("Phone Exposure", "https://www.google.com/search?q=%22" + encoded + "%22+phone");
        links.put("DuckDuckGo", "https://duckduckgo.com/?q=" + encoded);

        return links;
    }
}