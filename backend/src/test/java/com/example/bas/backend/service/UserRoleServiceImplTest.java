package com.example.bas.backend.service;

import com.example.bas.backend.BackendApplication;
import com.example.bas.backend.model.UserRole;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRoleServiceImplTest extends TestCase {

    @Autowired
    private UserRoleService userRoleService;

    @Test
    public void testSave() {
        UserRole role = UserRole.builder()
                .name("ROLE_RANDOM").build();
        assertTrue(userRoleService.save(role));
    }

    @Test
    public void testSaveAll() {
        UserRole role1 = UserRole.builder()
                .name("ROLE_RANDOM1").build();
        UserRole role2 = UserRole.builder()
                .name("ROLE_RANDOM2").build();
        List<UserRole> userRoleList = new ArrayList<>();
        userRoleList.add(role1);
        userRoleList.add(role2);
        assertTrue(userRoleService.saveAll(userRoleList));
    }

    @Test
    public void testDeleteById() {
        assertTrue(userRoleService.deleteById(2L));
    }

    @Test
    public void testFindById() {
        assertNotNull(userRoleService.findById(0L));
        assertNull(userRoleService.findById(10L));
    }

    @Test
    public void testFindAll() {
        assertEquals(2, userRoleService.findAll().size());
    }

    @Test
    public void testGetRoleByName() {
        assertNotNull(userRoleService.getRoleByName("ROLE_ADMIN"));
        assertNull(userRoleService.getRoleByName("NONEXISTENT"));
    }
}