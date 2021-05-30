package com.example.bas.backend.service;

import com.example.bas.backend.model.PasswordResetToken;
import com.example.bas.backend.repo.PasswordResetTokenRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PasswordResetTokenServiceImpl extends BasicServiceImpl<PasswordResetToken, PasswordResetTokenRepo, Long> implements PasswordResetTokenService {
    private static final Logger logger = Logger.getLogger(PasswordResetTokenServiceImpl.class.getName());

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepo passwordResetTokenRepo) {
        super(passwordResetTokenRepo);
    }

    @Override
    public PasswordResetToken findById(Long id) {
        PasswordResetToken passwordResetToken = null;
        try {
            passwordResetToken = repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Token doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return passwordResetToken;
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        PasswordResetToken passwordResetToken = null;
        try {
            passwordResetToken = repo.findByToken(token).orElseThrow(() -> new UsernameNotFoundException("Token doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return passwordResetToken;
    }

    @Override
    public List<PasswordResetToken> findAllByUserId(Long id) {
        List<PasswordResetToken> passwordResetToken = null;
        try {
            passwordResetToken = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("Token for that user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return passwordResetToken;
    }
}
