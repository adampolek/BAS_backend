package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EntryService extends BasicService<Entry, Long> {
    List<Entry> findAllByUserId(Long id);

    Entry findByEntryDateAndUserId(Date entryDate, Long id);

    boolean isEntrySet(Date entryDate, Long id);

    List<Entry> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id);

    Map<String, Map<String, Double>> generateEntryStats(Long id);
}
