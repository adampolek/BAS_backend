package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;
import com.example.bas.backend.model.PasswordResetToken;

import java.util.List;

public interface EntryService extends BasicService<Entry, Long> {
    List<Entry> findAllByUserId(Long id);
}
