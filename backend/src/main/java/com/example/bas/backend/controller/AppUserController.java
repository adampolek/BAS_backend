package com.example.bas.backend.controller;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.PasswordResetToken;
import com.example.bas.backend.model.forms.AppUserForm;
import com.example.bas.backend.model.forms.DailyEntry;
import com.example.bas.backend.model.forms.PasswordResetForm;
import com.example.bas.backend.security.configuration.JwtTokenUtil;
import com.example.bas.backend.security.models.JwtRequest;
import com.example.bas.backend.security.models.JwtResponse;
import com.example.bas.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/bas/user")
public class AppUserController {

    private static final Logger logger = Logger.getLogger(AppUserController.class.getName());

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserRoleService userRoleService;
    private final AppUserService appUserService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    private final EntryService entryService;
    private final AdditionalInfoService additionalInfoService;

    @GetMapping(value = "/role", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRole(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        user.setLastLogin(new Date());
        appUserService.update(user, false);
        return ResponseEntity.ok(user.getRole().getName());
    }

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<?> register(@Valid @RequestBody final AppUser user) {
        user.setRole(userRoleService.getRoleByName("ROLE_USER"));
        if (appUserService.save(user)) {
            emailService.send(user.getEmail(), "Account created", "Your account has been created " + user.getUsername() + "\nWelcome to our app!");
            return ResponseEntity.status(201).body("Successfully add " + getClass().getName());
        } else {
            return ResponseEntity.badRequest().body("User with this login or email already exists, try another one!");
        }

    }

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<?> login(@Valid @RequestBody final JwtRequest user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        AppUser userDetails = (AppUser) appUserService.loadUserByUsername(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwtTokenUtil.generateToken(userDetails, user.isRememberMe())));
    }

    @GetMapping(value = "/account", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAccount(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/get_all_entries", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllDailyEntries(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        List<DailyEntry> dailyEntries = appUserService.getAllEntriesForUserId(user.getId());
        return ResponseEntity.ok(dailyEntries);
    }

    @PutMapping(value = "/account", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateAccount(@Valid @RequestBody final AppUserForm form) {
        AppUser user = appUserService.findById(form.getId());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setHeight(form.getHeight());
        user.setGender(form.getGender());
        user.setBirthDate(form.getBirthDate());
        user.setEmail(form.getEmail());
        if (!user.getPassword().equals(form.getPassword())) {
            user.setPassword(form.getPassword());
            return appUserService.update(user, true) ? ResponseEntity.status(201).body("Successfully updated " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " added");
        }
        return appUserService.update(user, false) ? ResponseEntity.status(201).body("Successfully updated " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " added");
    }

    @DeleteMapping(value = "/account", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody final AppUserForm form) {
        AppUser user = appUserService.findById(form.getId());
        if (passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            emailService.send(user.getEmail(), "Account removed", "Your account has been removed.\nThank you for using our app!");
            appUserService.deleteById(user.getId());
            return ResponseEntity.status(200).body("Successfully updated " + getClass().getName());
        } else {
            return ResponseEntity.status(400).body("Couldn't remove user");
        }
    }

    @PostMapping(value = "/forgot-password", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody final AppUserForm form, HttpServletRequest request) {
        AppUser user = appUserService.findUserByEmail(form.getEmail());
        PasswordResetToken token = PasswordResetToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .build();
        token.setExpiryDate(30);
        passwordResetTokenService.save(token);
        String url = request.getScheme() + "://" + request.getServerName() + ":3000";
        emailService.send(user.getEmail(), "Reset password", url + "/change?token=" + token.getToken());
        return ResponseEntity.status(200).body("Successfully sent email");
    }

    @PostMapping(value = "/reset-password", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> postResetPassword(@Valid @RequestBody final PasswordResetForm form) {
        PasswordResetToken resetToken = passwordResetTokenService.findByToken(form.getToken());
        if (resetToken.isExpired()) {
            return ResponseEntity.badRequest().body("Token has expired, reset your password again");
        }
        AppUser user = appUserService.findById(resetToken.getUser().getId());
        user.setPassword(form.getPassword());
        if (appUserService.update(user, true)) {
            passwordResetTokenService.deleteById(resetToken.getId());
            return ResponseEntity.status(201).body("Successfully updated " + getClass().getName());

        }
        return ResponseEntity.badRequest().body("No " + getClass().getName() + " added");
    }
}
