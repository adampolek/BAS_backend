package com.example.bas.backend.service;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.repo.AppUserRepo;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl extends BasicServiceImpl<AppUser, AppUserRepo, Long> implements AppUserService {

    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(final AppUserRepo appUserRepo, @Lazy final PasswordEncoder passwordEncoder) {
        super(appUserRepo);
        this.passwordEncoder = passwordEncoder;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return user;
    }
}
