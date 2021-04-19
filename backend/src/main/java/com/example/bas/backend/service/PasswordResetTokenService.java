package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.PasswordResetToken;

import java.util.List;

public interface PasswordResetTokenService extends BasicService<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    List<PasswordResetToken> findAllByUserId(Long id);
}
