package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;
import com.example.bas.backend.repo.EntryRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EntryServiceImpl extends BasicServiceImpl<Entry, EntryRepo, Long> implements EntryService {
    private static final Logger logger = Logger.getLogger(EntryServiceImpl.class.getName());
    public EntryServiceImpl(EntryRepo entryRepo) {
        super(entryRepo);
    }

    @Override
    public List<Entry> findAllByUserId(Long id) {
        List<Entry> entry = null;
        try {
            entry = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("Entry for that user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entry;
    }

    @Override
    public Entry findByEntryDateAndUserId(Date entryDate, Long id) {
        Entry entry = null;
        try {
            entry = repo.findByEntryDateAndUserId(entryDate, id).orElseThrow(() -> new Exception("Entry for that date and user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entry;
    }

    @Override
    public List<Entry> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id) {
        List<Entry> entries = null;
        try {
            entries = repo.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, id).orElseThrow(() -> new Exception("Entry for that date range and user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entries;
    }
}
