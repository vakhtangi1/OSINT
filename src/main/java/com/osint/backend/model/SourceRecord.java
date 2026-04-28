package com.osint.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "source_records")
public class SourceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long personRecordId;
    private String platform;
    private String username;
    private String sourceUrl;

    @Column(columnDefinition = "TEXT")
    private String capturedText;

    private Double confidenceScore;
    private LocalDateTime createdAt;

    public SourceRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPersonRecordId() { return personRecordId; }
    public void setPersonRecordId(Long personRecordId) { this.personRecordId = personRecordId; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getCapturedText() { return capturedText; }
    public void setCapturedText(String capturedText) { this.capturedText = capturedText; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}