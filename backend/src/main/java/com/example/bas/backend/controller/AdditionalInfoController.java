package com.example.bas.backend.controller;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.forms.AdditionalInfoForm;
import com.example.bas.backend.service.AdditionalInfoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/bas/additional_info")
public class AdditionalInfoController extends BasicController<AdditionalInfoService, AdditionalInfo, Long> {

    private static final Logger logger = Logger.getLogger(AdditionalInfoController.class.getName());

    public AdditionalInfoController(AdditionalInfoService service) {
        super(service);
    }

    @GetMapping(value = "/day", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInfoFromDay(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate) {
        AdditionalInfo info = service.findByUserIdAndEntryDate(((AppUser) authentication.getPrincipal()).getId(), entryDate);
        if (info == null) {
            return ResponseEntity.status(400).body("Item doesn't exist");
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping(value = "/days", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInfoBetweenDays(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date stop) {
        List<AdditionalInfo> infos = service.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, ((AppUser) authentication.getPrincipal()).getId());
        if (infos == null) {
            return ResponseEntity.status(400).body("Item doesn't exist");
        }
        return ResponseEntity.ok(infos);
    }

    @PutMapping(value = "/updateEntry", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateEntry(@Valid @RequestBody AdditionalInfoForm form, Authentication authentication) {
        AdditionalInfo info = service.findById(form.getId());
        info.setCigarettesAmount(form.getCigarettesAmount());
        info.setSleepHours(form.getSleepHours());
        info.setGlassesOfWater(form.getGlassesOfWater());
        info.setTrainingHours(form.getTrainingHours());
        info.setAlcoholAmount(form.getAlcoholAmount());
        return service.save(info) ? ResponseEntity.status(201).body("Successfully update " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " updated");
    }

    @GetMapping(value = "/additional_info_stats", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAdditionalInfoStats(Authentication authentication) {
        Map<String, Map<String, Double>> responseMap = new HashMap<>();
        DoubleSummaryStatistics weeklyStats;
        DoubleSummaryStatistics monthlyStats;
        DoubleSummaryStatistics yearlyStats;
        Map<String, Double> tempMap;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        try {
            today = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            logger.warning(e.getMessage());
        }
        AppUser user = (AppUser) authentication.getPrincipal();
        AdditionalInfo userInfo = service.findByUserIdAndEntryDate(user.getId(), today);
        if (userInfo == null) {
            return ResponseEntity.status(400).body("Data for user doesn't exist");
        }
        List<AdditionalInfo> todayUserInfo = service.findAllByEntryDate(today);
        List<AdditionalInfo> hoursLessThan = service.findAllByEntryDateAndSleepHoursLessThan(today, userInfo.getSleepHours());
        if (todayUserInfo == null) {
            return ResponseEntity.status(400).body("Item doesn't exist");
        }
        if(hoursLessThan == null){
            hoursLessThan = new ArrayList<>();
        }
        LocalDateTime weekAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(6);
        LocalDateTime monthAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(29);
        LocalDateTime yearAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(364);

        Date weekAgoDate = Date.from(weekAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date monthAgoDate = Date.from(monthAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date yearAgoDate = Date.from(yearAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        List<AdditionalInfo> weeklyUserInfo = service.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(weekAgoDate, today, user.getId());
        List<AdditionalInfo> monthlyUserInfo = service.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(monthAgoDate, today, user.getId());
        List<AdditionalInfo> yearlyUserInfo = service.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(yearAgoDate, today, user.getId());

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("healthySleep", userInfo.getSleepHours() >= 6 && userInfo.getSleepHours() <= 8 ? 1.0 : 0.0);
        tempMap.put("sleepHoursPercentage", hoursLessThan.size() / (double) todayUserInfo.size() * 100);
        tempMap.put("weekly", weeklyStats.getAverage());
        tempMap.put("monthly", monthlyStats.getAverage());
        tempMap.put("yearly", yearlyStats.getAverage());
        responseMap.put("sleep", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", weeklyStats.getAverage());
        tempMap.put("monthly", monthlyStats.getAverage());
        tempMap.put("yearly", yearlyStats.getAverage());
        responseMap.put("alcohol", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", weeklyStats.getAverage());
        tempMap.put("monthly", monthlyStats.getAverage());
        tempMap.put("yearly", yearlyStats.getAverage());
        responseMap.put("cigarettes", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", weeklyStats.getAverage());
        tempMap.put("monthly", monthlyStats.getAverage());
        tempMap.put("yearly", yearlyStats.getAverage());
        responseMap.put("training", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", weeklyStats.getAverage());
        tempMap.put("monthly", monthlyStats.getAverage());
        tempMap.put("yearly", yearlyStats.getAverage());
        responseMap.put("water", tempMap);

        return ResponseEntity.ok(responseMap);
    }
}
