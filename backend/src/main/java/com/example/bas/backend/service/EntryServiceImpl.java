package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;
import com.example.bas.backend.repo.EntryRepo;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl extends BasicServiceImpl<Entry, EntryRepo,Long> implements EntryService {
    public EntryServiceImpl(EntryRepo entryRepo) {
        super(entryRepo);
    }
}
