package com.example.bas.backend.service;

import com.example.bas.backend.model.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends BasicService<AppUser, Long>, UserDetailsService {

}
