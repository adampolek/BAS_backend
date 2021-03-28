package com.example.bas.backend.service;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.repo.AppUserRepo;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl extends BasicServiceImpl<AppUser, AppUserRepo, Long> implements AppUserService {
    public AppUserServiceImpl(AppUserRepo appUserRepo) {
        super(appUserRepo);
    }
}
