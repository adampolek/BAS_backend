package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.repo.EntryRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntryServiceImpl extends BasicServiceImpl<Entry, EntryRepo, Long> implements EntryService {
    public EntryServiceImpl(EntryRepo entryRepo) {
        super(entryRepo);
    }

    @Override
    public List<Entry> findAllByUserId(Long id) {
        List<Entry> entry = null;
        try {
            entry = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("User with that id doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public Entry findByEntryDateAndUserId(Date entryDate, Long id) {
        Entry entry = null;
        try {
            entry = repo.findByEntryDateAndUserId(entryDate,id).orElseThrow(() -> new Exception("Entry hasn't been found"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public List<Entry> findAllByEntryDateBetweenAndUserId(Date start, Date stop, Long id) {
        List<Entry> entries = null;
        try {
            entries = repo.findAllByEntryDateBetweenAndUserId(start, stop,id).orElseThrow(() -> new Exception("Entry hasn't been found"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entries;
    }
}
