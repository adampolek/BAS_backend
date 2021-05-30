package com.example.bas.backend.service;

import com.example.bas.backend.BackendApplication;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.PasswordResetToken;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PasswordResetTokenServiceImplTest extends TestCase {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private AppUserService appUserService;

    @Test
    public void testSave() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findAll().get(0);
        PasswordResetToken token = null;
        try {
            token = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("AAAAAAAA")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertTrue(passwordResetTokenService.save(token));
    }

    @Test
    public void testSaveAll() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findAll().get(0);
        PasswordResetToken token1 = null;
        try {
            token1 = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("ABCD1234")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PasswordResetToken token2 = null;
        try {
            token2 = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-07-05"))
                    .user(user)
                    .token("EFGH1234")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<PasswordResetToken> tokenList = new ArrayList<>();
        tokenList.add(token1);
        tokenList.add(token2);
        assertTrue(passwordResetTokenService.saveAll(tokenList));
    }

    @Test
    public void testDeleteById() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findAll().get(0);
        PasswordResetToken token = null;
        try {
            token = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("XD")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        passwordResetTokenService.save(token);
        token = passwordResetTokenService.findByToken("XD");
        assertTrue(passwordResetTokenService.deleteById(token.getId()));
    }

    @Test
    public void testFindById() {
        assertNull(passwordResetTokenService.findById(10L));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findAll().get(0);
        PasswordResetToken token = null;
        try {
            token = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("XDDDD")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        passwordResetTokenService.save(token);
        token = passwordResetTokenService.findByToken("XDDDD");
        assertNotNull(passwordResetTokenService.findById(token.getId()));
    }

    @Test
    public void testFindAll() {
        assertEquals(1, passwordResetTokenService.findAll().size());
    }

    @Test
    public void testFindByToken() {
        assertNull(passwordResetTokenService.findByToken("AAAAADDASASADAAA"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findAll().get(0);
        PasswordResetToken token1 = null;
        try {
            token1 = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("AWAAAAAAA")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        passwordResetTokenService.save(token1);
        assertNotNull(passwordResetTokenService.findByToken("AWAAAAAAA"));
    }

    @Test
    public void testFindAllByUserId() {
        assertNull(passwordResetTokenService.findAllByUserId(10L));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findById(5L);
        PasswordResetToken token = null;
        try {
            token = PasswordResetToken.builder()
                    .expiryDate(format.parse("2021-06-05"))
                    .user(user)
                    .token("mozedziala")
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        passwordResetTokenService.save(token);
        assertEquals(1, passwordResetTokenService.findAllByUserId(5L).size());
    }
}