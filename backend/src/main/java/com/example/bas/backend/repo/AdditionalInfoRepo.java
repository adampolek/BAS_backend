package com.example.bas.backend.repo;

import com.example.bas.backend.model.AdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalInfoRepo extends JpaRepository<AdditionalInfo, Long> {
    Optional<List<AdditionalInfo>> findAllByUserId(Long id);
    Optional<AdditionalInfo> findByUserIdAndEntryDate(Long userId, Date entryDate);
    Optional<List<AdditionalInfo>> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long userId);
}
