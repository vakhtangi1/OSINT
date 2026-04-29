package com.osint.backend.model;

import com.osint.backend.security.CryptoUtil;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_records")
public class PersonRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long caseId;

    @Column(length = 2000)
    private String firstName;

    @Column(length = 2000)
    private String lastName;

    @Column(length = 2000)
    private String fullName;

    @Column(length = 2000)
    private String jobTitle;

    @Column(length = 2000)
    private String company;

    @Column(length = 2000)
    private String location;

    @Column(length = 2000)
    private String email;

    @Column(length = 2000)
    private String phoneNumber;

    @Column(length = 3000)
    private String profilePhotoUrl;

    @Column(length = 3000)
    private String sourceUrl;

    @Column(length = 1000)
    private String sourceType;

    @Column(columnDefinition = "TEXT")
    private String collectedText;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Double confidenceScore;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String searchText;

    @Column(length = 255)
    private String emailHash;

    @Column(length = 255)
    private String phoneHash;

    public PersonRecord() {
    }

    @PrePersist
    @PreUpdate
    private void encryptBeforeSave() {
        firstName = encrypt(firstName);
        lastName = encrypt(lastName);
        fullName = encrypt(fullName);
        jobTitle = encrypt(jobTitle);
        company = encrypt(company);
        location = encrypt(location);
        email = encrypt(email);
        phoneNumber = encrypt(phoneNumber);
        profilePhotoUrl = encrypt(profilePhotoUrl);
        sourceUrl = encrypt(sourceUrl);
        sourceType = encrypt(sourceType);
        collectedText = encrypt(collectedText);
        notes = encrypt(notes);
        searchText = encrypt(searchText);
    }

    @PostLoad
    private void decryptAfterLoad() {
        firstName = decrypt(firstName);
        lastName = decrypt(lastName);
        fullName = decrypt(fullName);
        jobTitle = decrypt(jobTitle);
        company = decrypt(company);
        location = decrypt(location);
        email = decrypt(email);
        phoneNumber = decrypt(phoneNumber);
        profilePhotoUrl = decrypt(profilePhotoUrl);
        sourceUrl = decrypt(sourceUrl);
        sourceType = decrypt(sourceType);
        collectedText = decrypt(collectedText);
        notes = decrypt(notes);
        searchText = decrypt(searchText);
    }

    private String encrypt(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        if (value.startsWith("v1:")) {
            return value;
        }

        return CryptoUtil.encrypt(value);
    }

    private String decrypt(String value) {
        return CryptoUtil.decrypt(value);
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = clean(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = clean(lastName);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = clean(fullName);
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = clean(jobTitle);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = clean(company);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = clean(location);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = clean(email);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = clean(phoneNumber);
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = clean(profilePhotoUrl);
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = clean(sourceUrl);
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = clean(sourceType);
    }

    public String getCollectedText() {
        return collectedText;
    }

    public void setCollectedText(String collectedText) {
        this.collectedText = clean(collectedText);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = clean(notes);
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = clean(searchText);
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }
}