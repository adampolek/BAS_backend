package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.repo.AdditionalInfoRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

@Service
public class AdditionalInfoServiceImpl extends BasicServiceImpl<AdditionalInfo, AdditionalInfoRepo, Long> implements AdditionalInfoService {
    private static final Logger logger = Logger.getLogger(AdditionalInfoServiceImpl.class.getName());
    public AdditionalInfoServiceImpl(AdditionalInfoRepo additionalInfoRepo) {
        super(additionalInfoRepo);
    }

    @Override
    public List<AdditionalInfo> findAllByUserId(Long id) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("Info for user with that id doesn't exist."));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return info;
    }

    @Override
    public AdditionalInfo findByUserIdAndEntryDate(Long userId, Date entryDate) {
        AdditionalInfo info = null;
        try {
            info = repo.findByUserIdAndEntryDate(userId, entryDate).orElseThrow(() -> new UsernameNotFoundException("Info for that user and date doesn't exist."));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return info;
    }

    @Override
    public List<AdditionalInfo> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, id).orElseThrow(() -> new UsernameNotFoundException("Info for that date range and user doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return info;
    }

    @Override
    public List<AdditionalInfo> findAllByEntryDateAndSleepHoursLessThan(Date date, Double sleepHours) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByEntryDateAndSleepHoursLessThan(date, sleepHours).orElseThrow(() -> new UsernameNotFoundException("Info for that date and sleep hours doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return info;
    }

    @Override
    public List<AdditionalInfo> findAllByEntryDate(Date date) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByEntryDate(date).orElseThrow(() -> new UsernameNotFoundException("Info for that date doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return info;
    }

    @Override
    public Map<String, Map<String, Double>> generateAdditionalStats(Long id) {
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
        AdditionalInfo userInfo = this.findByUserIdAndEntryDate(id, today);
        if (userInfo == null) {
            return null;
        }
        List<AdditionalInfo> todayUserInfo = this.findAllByEntryDate(today);
        List<AdditionalInfo> hoursLessThan = this.findAllByEntryDateAndSleepHoursLessThan(today, userInfo.getSleepHours());
        if (todayUserInfo == null) {
            return null;
        }
        if (hoursLessThan == null) {
            hoursLessThan = new ArrayList<>();
        }
        LocalDateTime weekAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(6);
        LocalDateTime monthAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(29);
        LocalDateTime yearAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(364);

        Date weekAgoDate = Date.from(weekAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date monthAgoDate = Date.from(monthAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date yearAgoDate = Date.from(yearAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        List<AdditionalInfo> weeklyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(weekAgoDate, today, id);
        List<AdditionalInfo> monthlyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(monthAgoDate, today, id);
        List<AdditionalInfo> yearlyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(yearAgoDate, today, id);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getSleepHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("healthySleep", userInfo.getSleepHours() >= 6 && userInfo.getSleepHours() <= 8 ? 1.0 : 0.0);
        tempMap.put("sleepHoursPercentage", (double) Math.round(hoursLessThan.size() / (double) todayUserInfo.size() * 100.0));
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("sleep", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getAlcoholAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("alcohol", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getCigarettesAmount).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("cigarettes", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getTrainingHours).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("training", tempMap);

        weeklyStats = weeklyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(AdditionalInfo::getGlassesOfWater).map(num -> num == null ? 0 : num).mapToDouble(Double::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("water", tempMap);

        return responseMap;
    }
}
