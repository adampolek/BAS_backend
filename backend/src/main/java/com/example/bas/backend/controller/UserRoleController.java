package com.example.bas.backend.controller;

import com.example.bas.backend.model.UserRole;
import com.example.bas.backend.service.UserRoleService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/bas/user_role")
public class UserRoleController extends BasicController<UserRoleService, UserRole,Long>{
    public UserRoleController(UserRoleService service) {
        super(service);
    }
}
