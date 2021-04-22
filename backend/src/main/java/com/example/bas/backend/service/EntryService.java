package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;

import java.util.Date;
import java.util.List;

public interface EntryService extends BasicService<Entry, Long> {
    List<Entry> findAllByUserId(Long id);

    Entry findByEntryDate(Date entryDate);

    List<Entry> findAllByEntryDateBetween(Date start, Date stop);
}
