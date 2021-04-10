package com.example.bas.backend.service;

import com.example.bas.backend.model.UserRole;
import com.example.bas.backend.repo.UserRoleRepo;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends BasicServiceImpl<UserRole, UserRoleRepo,Long> implements UserRoleService {

    public UserRoleServiceImpl(final UserRoleRepo userRoleRepo) {
        super(userRoleRepo);
    }

    @Override
    public UserRole getRoleByName(String name) {
        return repo.getByName(name);
    }
}
