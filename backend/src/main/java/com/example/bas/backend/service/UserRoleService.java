package com.example.bas.backend.service;

import com.example.bas.backend.model.UserRole;

public interface UserRoleService extends BasicService<UserRole, Long> {
    UserRole getRoleByName(String name);
}
