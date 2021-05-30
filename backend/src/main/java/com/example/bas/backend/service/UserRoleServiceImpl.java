package com.example.bas.backend.service;

import com.example.bas.backend.model.UserRole;
import com.example.bas.backend.repo.UserRoleRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserRoleServiceImpl extends BasicServiceImpl<UserRole, UserRoleRepo, Long> implements UserRoleService {
    private static final Logger logger = Logger.getLogger(UserRoleServiceImpl.class.getName());

    public UserRoleServiceImpl(final UserRoleRepo userRoleRepo) {
        super(userRoleRepo);
    }

    @Override
    public UserRole getRoleByName(String name) {
        return repo.getByName(name);
    }

    @Override
    public UserRole findById(Long id) {
        UserRole role = null;
        try {
            role = repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Role with that id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return role;
    }
}
