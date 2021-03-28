package com.example.bas.backend.repo;

import com.example.bas.backend.model.AdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalInfoRepo extends JpaRepository<AdditionalInfo, Long> {
}
