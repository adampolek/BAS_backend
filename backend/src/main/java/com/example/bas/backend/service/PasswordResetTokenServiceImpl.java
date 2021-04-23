package com.example.bas.backend.service;

import com.example.bas.backend.model.PasswordResetToken;
import com.example.bas.backend.repo.PasswordResetTokenRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordResetTokenServiceImpl extends BasicServiceImpl<PasswordResetToken, PasswordResetTokenRepo, Long> implements PasswordResetTokenService {
    public PasswordResetTokenServiceImpl(PasswordResetTokenRepo passwordResetTokenRepo) {
        super(passwordResetTokenRepo);
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        PasswordResetToken passwordResetToken = null;
        try {
            passwordResetToken = repo.findByToken(token).orElseThrow(() -> new UsernameNotFoundException("Token doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return passwordResetToken;
    }

    @Override
    public List<PasswordResetToken> findAllByUserId(Long id) {
        List<PasswordResetToken> passwordResetToken = null;
        try {
            passwordResetToken = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("Token doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return passwordResetToken;
    }
}
