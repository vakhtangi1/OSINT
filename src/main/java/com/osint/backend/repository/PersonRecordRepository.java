package com.osint.backend.repository;

import com.osint.backend.model.PersonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {

    List<PersonRecord> findByCaseId(Long caseId);

    List<PersonRecord> findByFullNameContainingIgnoreCase(String fullName);

    List<PersonRecord> findByEmailHashOrPhoneHash(String emailHash, String phoneHash);

    @Query("""
            SELECT p FROM PersonRecord p
            WHERE LOWER(p.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<PersonRecord> searchBySearchText(String query);
}