package com.example.bas.backend.service;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.repo.AppUserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl extends BasicServiceImpl<AppUser, AppUserRepo, Long> implements AppUserService {
    public AppUserServiceImpl(AppUserRepo appUserRepo) {
        super(appUserRepo);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = new AppUser();
        try {
            user = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User with that login doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
