package com.osint.backend.dto;

import java.util.List;

public class DorkingResponse {

    private String keyword;
    private String type;
    private List<String> queries;

    public DorkingResponse() {
    }

    public DorkingResponse(String keyword, String type, List<String> queries) {
        this.keyword = keyword;
        this.type = type;
        this.queries = queries;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }
}