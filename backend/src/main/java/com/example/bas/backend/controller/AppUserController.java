package com.example.bas.backend.controller;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.service.AppUserService;
import com.example.bas.backend.service.AppUserServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bas/app_user")
public class AppUserController extends BasicController<AppUserService, AppUser,Long>{
    public AppUserController(AppUserService service) {
        super(service);
    }
}
