package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.model.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final EmailService emailService;
    private final EntryService entryService;
    private final AdditionalInfoService additionalInfoService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final AppUserService appUserService;
    private static final List<Integer> daysToCheck = Stream.iterate(new int[]{1, 2}, t -> new int[]{t[1], t[0] + t[1]}).limit(13).map(n -> n[0]).collect(Collectors.toList());

    @Scheduled(cron = "0 0 20 * * ?")
    public void checkLastLogin() {
        Date today = new Date();
        List<AppUser> userList = appUserService.findAll();
        for (AppUser user : userList) {
            if (user.getLastLogin() == null) continue;
            long diffInMs = Math.abs(today.getTime() - user.getLastLogin().getTime());
            Integer diff = Math.toIntExact(TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS));
            if (diff.equals(daysToCheck.get(daysToCheck.size() - 1))) {
                emailService.send(user.getEmail(), "Account removal", String.format("%s, your account has been removed due to inactivity and your data has been erased, thank you for using our app.", user.getUsername()));
                List<Entry> userEntries = entryService.findAllByUserId(user.getId());
                if (userEntries != null) {
                    for (Entry entry : userEntries) {
                        entryService.deleteById(entry.getId());
                    }
                }
                List<AdditionalInfo> userInfos = additionalInfoService.findAllByUserId(user.getId());
                if (userInfos != null) {
                    for (AdditionalInfo info : userInfos) {
                        additionalInfoService.deleteById(info.getId());
                    }
                }
                List<PasswordResetToken> userResetTokens = passwordResetTokenService.findAllByUserId(user.getId());
                if (userResetTokens != null) {
                    for (PasswordResetToken resetToken : userResetTokens) {
                        passwordResetTokenService.deleteById(resetToken.getId());
                    }
                }
                appUserService.deleteById(user.getId());
            } else if (diff == daysToCheck.get(daysToCheck.size() - 1) - 1) {
                emailService.send(user.getEmail(), "Account removal warning - 1 day", String.format("%s, you haven't been active for %d days, your account will be removed tomorrow at 8 PM.", user.getUsername(), diff));
            } else if (diff == daysToCheck.get(daysToCheck.size() - 1) - 3) {
                emailService.send(user.getEmail(), "Account removal warning - 3 days", String.format("%s, you haven't been active for %d days, your account will be removed in 3 days.", user.getUsername(), diff));
            } else if (diff == daysToCheck.get(daysToCheck.size() - 1) - 7) {
                emailService.send(user.getEmail(), "Account removal warning - week", String.format("%s, you haven't been active for %d days, your account will be removed in a week.", user.getUsername(), diff));
            } else if (daysToCheck.contains(diff)) {
                emailService.send(user.getEmail(), "Inactivity reminder", String.format("%s, you haven't been active for %d %s, we are worried!", user.getUsername(), diff, diff == 1 ? "day" : "days"));
            }
        }
    }
}
