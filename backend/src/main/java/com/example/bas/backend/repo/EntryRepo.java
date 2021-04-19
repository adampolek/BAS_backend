package com.example.bas.backend.repo;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Long> {
    Optional<List<Entry>> findAllByUserId(Long id);
}
