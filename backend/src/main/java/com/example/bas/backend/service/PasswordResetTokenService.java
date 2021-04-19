package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.PasswordResetToken;

public interface PasswordResetTokenService extends BasicService<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
