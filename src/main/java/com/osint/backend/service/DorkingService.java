package com.osint.backend.service;

import com.osint.backend.dto.DorkingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DorkingService {

    public DorkingResponse generateQueries(String keyword, String type) {
        List<String> queries = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return new DorkingResponse("", type, queries);
        }

        String safeKeyword = keyword.trim();

        switch (type.toLowerCase()) {
            case "github":
                queries.add("site:github.com \"" + safeKeyword + "\"");
                queries.add("site:github.com inurl:" + safeKeyword);
                queries.add("site:github.com intitle:\"" + safeKeyword + "\"");
                break;

            case "documents":
                queries.add("filetype:pdf \"" + safeKeyword + "\"");
                queries.add("filetype:doc \"" + safeKeyword + "\"");
                queries.add("filetype:docx \"" + safeKeyword + "\"");
                queries.add("filetype:xls \"" + safeKeyword + "\"");
                queries.add("filetype:xlsx \"" + safeKeyword + "\"");
                break;

            case "social":
                queries.add("site:linkedin.com/in \"" + safeKeyword + "\"");
                queries.add("site:x.com \"" + safeKeyword + "\"");
                queries.add("site:facebook.com \"" + safeKeyword + "\"");
                queries.add("site:instagram.com \"" + safeKeyword + "\"");
                break;

            case "general":
            default:
                queries.add("\"" + safeKeyword + "\"");
                queries.add("intitle:\"" + safeKeyword + "\"");
                queries.add("inurl:" + safeKeyword);
                queries.add("site:github.com \"" + safeKeyword + "\"");
                queries.add("filetype:pdf \"" + safeKeyword + "\"");
                break;
        }

        return new DorkingResponse(safeKeyword, type, queries);
    }
}