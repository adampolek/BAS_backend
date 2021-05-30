package com.example.bas.backend.service;

import com.example.bas.backend.BackendApplication;
import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
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
public class AdditionalInfoServiceImplTest extends TestCase {

    @Autowired
    private AdditionalInfoService additionalInfoService;

    @Autowired
    private AppUserService appUserService;

    @Test
    public void testSave() {
        AppUser user = appUserService.findUserByEmail("ktos353@wp.pl");
        AdditionalInfo info = AdditionalInfo.builder()
                .trainingHours(5.0)
                .sleepHours(5.0)
                .user(user)
                .glassesOfWater(2)
                .entryDate(new Date())
                .cigarettesAmount(1)
                .alcoholAmount(2)
                .build();
        assertTrue(additionalInfoService.save(info));
    }

    @Test
    public void testSaveAll() {
        AppUser user = appUserService.findUserByEmail("ktos353@wp.pl");
        AdditionalInfo info = AdditionalInfo.builder()
                .trainingHours(5.0)
                .sleepHours(5.0)
                .user(user)
                .glassesOfWater(2)
                .entryDate(new Date())
                .cigarettesAmount(1)
                .alcoholAmount(2)
                .build();
        AdditionalInfo info2 = AdditionalInfo.builder()
                .trainingHours(5.0)
                .sleepHours(5.0)
                .user(user)
                .glassesOfWater(2)
                .entryDate(new Date())
                .cigarettesAmount(1)
                .alcoholAmount(2)
                .build();
        AdditionalInfo info3 = AdditionalInfo.builder()
                .trainingHours(5.0)
                .sleepHours(5.0)
                .user(user)
                .glassesOfWater(2)
                .entryDate(new Date())
                .cigarettesAmount(1)
                .alcoholAmount(2)
                .build();
        List<AdditionalInfo> infoList = new ArrayList<>();
        infoList.add(info);
        infoList.add(info2);
        infoList.add(info3);
        assertTrue(additionalInfoService.saveAll(infoList));
    }

    @Test
    public void testDeleteById() {
        assertTrue(additionalInfoService.deleteById(0L));
    }

    @Test
    public void testFindById() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppUser user = appUserService.findById(5L);
        AdditionalInfo info = new AdditionalInfo();
        try {
            info = AdditionalInfo.builder()
                    .id(4000L)
                    .alcoholAmount(15)
                    .cigarettesAmount(2)
                    .entryDate(simpleDateFormat.parse("2021-05-28"))
                    .glassesOfWater(18)
                    .sleepHours(4.5)
                    .trainingHours(5.75)
                    .user(user)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(info, additionalInfoService.findById(4000L));
    }

    @Test
    public void testFindAll() {
        assertEquals(4800, additionalInfoService.findAll().size());
    }

    @Test
    public void testFindAllByUserId() {
        assertEquals(800, additionalInfoService.findAllByUserId(0L).size());
        assertNull(additionalInfoService.findAllByUserId(10L));
    }

    @Test
    public void testFindByUserIdAndEntryDate() {
        AppUser user = appUserService.findById(5L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AdditionalInfo info = new AdditionalInfo();
        try {
            info = AdditionalInfo.builder()
                    .id(4000L)
                    .alcoholAmount(15)
                    .cigarettesAmount(2)
                    .entryDate(simpleDateFormat.parse("2021-05-28"))
                    .glassesOfWater(18)
                    .sleepHours(4.5)
                    .trainingHours(5.75)
                    .user(user)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            assertEquals(info, additionalInfoService.findByUserIdAndEntryDate(5L, simpleDateFormat.parse("2021-05-28")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            assertNull(additionalInfoService.findByUserIdAndEntryDate(10L, simpleDateFormat.parse("2021-05-28")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            assertEquals(7, additionalInfoService.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(simpleDateFormat.parse("2021-05-22"), simpleDateFormat.parse("2021-05-28"), 5L).size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            assertNull(additionalInfoService.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(simpleDateFormat.parse("2021-05-29"), simpleDateFormat.parse("2021-05-28"), 5L));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAllByEntryDateAndSleepHoursLessThan() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            assertEquals(1, additionalInfoService.findAllByEntryDateAndSleepHoursLessThan(simpleDateFormat.parse("2021-05-28"), 5.0).size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAllByEntryDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            assertEquals(5, additionalInfoService.findAllByEntryDate(simpleDateFormat.parse("2021-05-28")).size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            assertNull(additionalInfoService.findAllByEntryDate(simpleDateFormat.parse("2021-06-30")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateAdditionalStats() {
        assertNull(additionalInfoService.generateAdditionalStats(10L));
        Map<String, Map<String, Double>> stats = additionalInfoService.generateAdditionalStats(5L);
        List<String> outsideKeys = Arrays.asList("cigarettes", "sleep", "alcohol", "training", "water");
        List<String> insideKeys = Arrays.asList("weekly", "monthly", "yearly");
        List<String> sleepKeys = Arrays.asList("weekly", "monthly", "yearly", "healthySleep", "sleepHoursPercentage");
        assertTrue(stats.keySet().containsAll(outsideKeys));
        for (String outsideKey : outsideKeys) {
            if (outsideKey.equals("sleep")) {
                assertTrue(stats.get(outsideKey).keySet().containsAll(sleepKeys));
            } else {
                assertTrue(stats.get(outsideKey).keySet().containsAll(insideKeys));
            }
        }
    }
}