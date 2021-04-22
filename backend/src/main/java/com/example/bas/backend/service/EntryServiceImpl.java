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
        List<Entry> entry = new ArrayList<>();
        try {
            entry = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("User with that id doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public Entry findByEntryDate(Date entryDate) {
        Entry entry = new Entry();
        try {
            entry = repo.findByEntryDate(entryDate).orElseThrow(() -> new Exception("Entry hasn't been found"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public List<Entry> findAllByEntryDateBetween(Date start, Date stop) {
        List<Entry> entries = new ArrayList<>();
        try {
            entries = repo.findAllByEntryDateBetween(start, stop).orElseThrow(() -> new Exception("Entry hasn't been found"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return entries;
    }
}
