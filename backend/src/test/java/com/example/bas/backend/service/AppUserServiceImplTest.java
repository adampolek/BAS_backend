package com.example.bas.backend.service;

import com.example.bas.backend.BackendApplication;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.PasswordResetToken;
import com.example.bas.backend.model.UserRole;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppUserServiceImplTest extends TestCase {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSave() {
        UserRole role = userRoleService.getRoleByName("ROLE_ADMIN");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = null;
        try {
            user = AppUser.builder()
                    .birthDate(simpleDateFormat.parse("1997-05-16"))
                    .email("test@gmail.com")
                    .firstName("Andrzej")
                    .lastName("Kowalczyk")
                    .lastLogin(simpleDateFormat.parse("2020-05-29"))
                    .gender("male")
                    .height(185)
                    .username("anko")
                    .password(passwordEncoder.encode("admin"))
                    .role(role)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertTrue(appUserService.save(user));
        assertFalse(appUserService.save(user));
    }

    @Test
    public void testSaveAll() {
        UserRole role = userRoleService.getRoleByName("ROLE_ADMIN");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user1 = null;
        AppUser user2 = null;
        try {
            user1 = AppUser.builder()
                    .birthDate(simpleDateFormat.parse("1997-05-16"))
                    .email("abcdefgh@gmail.com")
                    .firstName("Andrzej")
                    .lastName("Kowalczyk")
                    .lastLogin(simpleDateFormat.parse("2020-05-29"))
                    .gender("male")
                    .height(185)
                    .username("anko1")
                    .password(passwordEncoder.encode("admin"))
                    .role(role)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            user2 = AppUser.builder()
                    .birthDate(simpleDateFormat.parse("1997-05-16"))
                    .email("abcdefgh2@gmail.com")
                    .firstName("Andrzej")
                    .lastName("Kowalczyk")
                    .lastLogin(simpleDateFormat.parse("2020-05-29"))
                    .gender("male")
                    .height(185)
                    .username("anko2")
                    .password(passwordEncoder.encode("admin"))
                    .role(role)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<AppUser> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        assertTrue(appUserService.saveAll(users));
    }

    @Test
    public void testDeleteById() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findById(0L);
        PasswordResetToken passwordResetToken = null;
        try {
            passwordResetToken = PasswordResetToken.builder()
                    .token("abcd")
                    .user(user)
                    .expiryDate(simpleDateFormat.parse("2021-06-01")).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        passwordResetTokenService.save(passwordResetToken);
        assertTrue(appUserService.deleteById(0L));
    }

    @Test
    public void testFindById() {
        assertNotNull(appUserService.findById(1L));
    }

    @Test
    public void testFindAll() {
        assertEquals(5, appUserService.findAll().size());
    }

    @Test
    public void testLoadUserByUsername() {
        assertNotNull(appUserService.loadUserByUsername("admin"));
        assertNull(appUserService.loadUserByUsername("nonexistent"));
    }

    @Test
    public void testUpdate() {
        AppUser user1 = appUserService.findById(1L);
        AppUser user2 = appUserService.findById(2L);
        user1.setPassword("newpassword");
        user1.setHeight(190);
        user2.setHeight(158);
        assertTrue(appUserService.update(user1, true));
        assertTrue(appUserService.update(user2, false));
    }

    @Test
    public void testFindUserByEmail() {
        assertNotNull(appUserService.findUserByEmail("mazsak97@gmail.com"));
        assertNull(appUserService.findUserByEmail("kogosniema@wp.pl"));
    }

    @Test
    public void testGetAllEntriesForUserId() {
        appUserService.deleteById(3L);
        assertTrue(appUserService.getAllEntriesForUserId(3L).isEmpty());
        assertFalse(appUserService.getAllEntriesForUserId(1L).isEmpty());
    }
}