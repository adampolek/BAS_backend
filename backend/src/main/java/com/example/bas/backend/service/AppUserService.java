package com.example.bas.backend.service;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.forms.DailyEntry;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AppUserService extends BasicService<AppUser, Long>, UserDetailsService {

    boolean update(AppUser user, boolean changedPassword);

    AppUser findUserByEmail(String userEmail);

    List<DailyEntry> getAllEntriesForUserId(Long id);
}
