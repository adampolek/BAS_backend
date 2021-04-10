package com.example.bas.backend.controller;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.security.configuration.JwtTokenUtil;
import com.example.bas.backend.security.models.JwtRequest;
import com.example.bas.backend.security.models.JwtResponse;
import com.example.bas.backend.service.AppUserService;
import com.example.bas.backend.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/bas/user")
public class AppUserController {

    private final AuthenticationManager authenticationManager;
    private final UserRoleService userRoleService;
    private final AppUserService appUserService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping(value = "/role", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRole(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(user.getRole().getName());
    }

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<?> register(@Valid @RequestBody final AppUser user) {
        user.setRole(userRoleService.getRoleByName("ROLE_USER"));
        return appUserService.save(user) ? ResponseEntity.status(201).body("Successfully add " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " added");
    }

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<?> login(@Valid @RequestBody final JwtRequest user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        AppUser userDetails = (AppUser) appUserService.loadUserByUsername(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwtTokenUtil.generateToken(userDetails)));
    }
}
