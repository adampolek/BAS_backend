package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.model.PasswordResetToken;
import com.example.bas.backend.model.forms.DailyEntry;
import com.example.bas.backend.repo.AppUserRepo;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AppUserServiceImpl extends BasicServiceImpl<AppUser, AppUserRepo, Long> implements AppUserService {

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(AppUserServiceImpl.class.getName());

    private final AdditionalInfoService additionalInfoService;
    private final EntryService entryService;
    private final PasswordResetTokenService passwordResetTokenService;

    public AppUserServiceImpl(final AppUserRepo appUserRepo, @Lazy final PasswordEncoder passwordEncoder, AdditionalInfoService additionalInfoService, EntryService entryService, PasswordResetTokenService passwordResetTokenService) {
        super(appUserRepo);
        this.passwordEncoder = passwordEncoder;
        this.additionalInfoService = additionalInfoService;
        this.entryService = entryService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @Override
    public boolean deleteById(Long id) {
        List<AdditionalInfo> additionalInfos = additionalInfoService.findAllByUserId(id);
        List<Entry> entries = entryService.findAllByUserId(id);
        List<PasswordResetToken> passwordResetTokens = passwordResetTokenService.findAllByUserId(id);
        if (additionalInfos != null) {
            additionalInfos.forEach(additionalInfo -> {
                additionalInfoService.deleteById(additionalInfo.getId());
            });
        }
        if (entries != null) {
            entries.forEach(entry -> {
                entryService.deleteById(entry.getId());
            });
        }
        if (passwordResetTokens != null) {
            passwordResetTokens.forEach(passwordResetToken -> {
                passwordResetTokenService.deleteById(passwordResetToken.getId());
            });
        }
        return super.deleteById(id);
    }

    @Override
    public boolean save(AppUser object) {
        if (!repo.existsByUsername(object.getUsername())) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
            return super.save(object);
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = null;
        try {
            user = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User with that login doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return user;
    }

    @Override
    public boolean update(AppUser user, boolean changedPassword) {
        if (changedPassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return super.save(user);
    }

    @Override
    public AppUser findUserByEmail(String userEmail) {
        AppUser user = null;
        try {
            user = repo.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User with that email doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return user;
    }

    @Override
    public List<DailyEntry> getAllEntriesForUserId(Long id) {
        List<DailyEntry> dailyEntries = new ArrayList<>();
        List<AdditionalInfo> infoList = additionalInfoService.findAllByUserId(id);
        List<Entry> entryList = entryService.findAllByUserId(id);
        if (entryList == null) {
            return dailyEntries;
        }
        for (Entry entry : entryList) {
            AdditionalInfo thisDayInfo = null;
            for (AdditionalInfo info : infoList) {
                if (entry.getEntryDate().equals(info.getEntryDate())) {
                    thisDayInfo = info;
                    break;
                }
            }
            DailyEntry dailyEntry = DailyEntry.builder()
                    .entryDate(entry.getEntryDate())
                    .cigarettesAmount(thisDayInfo != null ? thisDayInfo.getCigarettesAmount() : null)
                    .sleepHours(thisDayInfo != null ? thisDayInfo.getSleepHours() : null)
                    .glassesOfWater(thisDayInfo != null ? thisDayInfo.getGlassesOfWater() : null)
                    .trainingHours(thisDayInfo != null ? thisDayInfo.getTrainingHours() : null)
                    .alcoholAmount(thisDayInfo != null ? thisDayInfo.getAlcoholAmount() : null)
                    .glucose(entry.getGlucose())
                    .bloodPressure(entry.getBloodPressure())
                    .insulin(entry.getInsulin())
                    .weight(entry.getWeight())
                    .healthy(entry.getHealthy())
                    .build();
            dailyEntries.add(dailyEntry);
        }
        return dailyEntries;
    }
}
