package com.osint.backend.repository;

import com.osint.backend.model.PersonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {

    List<PersonRecord> findByCaseId(Long caseId);

    List<PersonRecord> findByFullNameContainingIgnoreCase(String fullName);

    @Query("""
            SELECT p FROM PersonRecord p
            WHERE LOWER(p.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
               OR p.emailHash = :emailHash
               OR p.phoneHash = :phoneHash
            ORDER BY p.createdAt DESC
            """)
    List<PersonRecord> globalSearch(
            @Param("query") String query,
            @Param("emailHash") String emailHash,
            @Param("phoneHash") String phoneHash
    );
}