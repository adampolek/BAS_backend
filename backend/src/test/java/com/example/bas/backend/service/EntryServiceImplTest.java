package com.example.bas.backend.service;

import com.example.bas.backend.BackendApplication;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EntryServiceImplTest extends TestCase {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private EntryService entryService;

    @Test
    public void testSave() {
        AppUser user = appUserService.findById(1L);
        Entry entry = Entry.builder()
                .user(user)
                .entryDate(new Date())
                .bloodPressure(120)
                .glucose(120)
                .insulin(0)
                .healthy(true)
                .weight(90.0)
                .build();
        assertTrue(entryService.save(entry));
    }

    @Test
    public void testSaveAll() {
        AppUser user = appUserService.findById(1L);
        Entry entry1 = Entry.builder()
                .user(user)
                .entryDate(new Date())
                .bloodPressure(120)
                .glucose(120)
                .insulin(0)
                .healthy(true)
                .weight(90.0)
                .build();
        Entry entry2 = Entry.builder()
                .user(user)
                .entryDate(new Date())
                .bloodPressure(120)
                .glucose(120)
                .insulin(0)
                .healthy(true)
                .weight(90.0)
                .build();
        List<Entry> entryList = new ArrayList<>();
        entryList.add(entry1);
        entryList.add(entry2);
        assertTrue(entryService.saveAll(entryList));
    }

    @Test
    public void testDeleteById() {
        assertTrue(entryService.deleteById(3549L));
    }

    @Test
    public void testFindById() {
        assertNotNull(entryService.findById(1000L));
        assertNull(entryService.findById(100000L));
    }

    @Test
    public void testFindAll() {
        assertFalse(entryService.findAll().isEmpty());
    }

    @Test
    public void testFindAllByUserId() {
        assertNull(entryService.findAllByUserId(10L));
        assertEquals(800, entryService.findAllByUserId(1L).size());
    }

    @Test
    public void testFindByEntryDateAndUserId() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            assertNull(entryService.findByEntryDateAndUserId(format.parse("2021-05-28"), 10L));
            assertNotNull(entryService.findByEntryDateAndUserId(format.parse("2021-05-28"), 1L));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            assertNull(entryService.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(format.parse("2021-05-21"), format.parse("2021-05-28"), 10L));
            assertEquals(7, entryService.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(format.parse("2021-05-22"), format.parse("2021-05-28"), 5L).size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateEntryStats() {
        assertNull(entryService.generateEntryStats(10L));
        Map<String, Map<String, Double>> stats = entryService.generateEntryStats(5L);
        List<String> outsideKeys = Arrays.asList("weight", "bloodPressure", "glucose", "insulin", "healthy");
        List<String> insideKeys = Arrays.asList("weekly", "monthly", "yearly");
        assertTrue(stats.keySet().containsAll(outsideKeys));
        for (String outsideKey : outsideKeys) {
            assertTrue(stats.get(outsideKey).keySet().containsAll(insideKeys));
        }
    }
}