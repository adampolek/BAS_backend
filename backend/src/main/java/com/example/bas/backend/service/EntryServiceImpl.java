package com.example.bas.backend.service;

import com.example.bas.backend.model.Entry;
import com.example.bas.backend.repo.EntryRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EntryServiceImpl extends BasicServiceImpl<Entry, EntryRepo, Long> implements EntryService {
    private static final Logger logger = Logger.getLogger(EntryServiceImpl.class.getName());

    public EntryServiceImpl(EntryRepo entryRepo) {
        super(entryRepo);
    }

    @Override
    public List<Entry> findAllByUserId(Long id) {
        List<Entry> entry = null;
        try {
            entry = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("Entry for that user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entry;
    }

    @Override
    public Entry findById(Long id) {
        Entry entry = null;
        try {
            entry = repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Entry with that id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entry;
    }

    @Override
    public Entry findByEntryDateAndUserId(Date entryDate, Long id) {
        Entry entry = null;
        try {
            entry = repo.findByEntryDateAndUserId(entryDate, id).orElseThrow(() -> new Exception("Entry for that date and user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entry;
    }

    @Override
    public boolean isEntrySet(Date entryDate, Long id) {
        return repo.existsByEntryDateAndUserId(entryDate, id);
    }

    @Override
    public List<Entry> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id) {
        List<Entry> entries = null;
        try {
            entries = repo.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, id).orElseThrow(() -> new Exception("Entry for that date range and user id doesn't exist"));
        } catch (final Exception e) {
            logger.warning(e.getMessage());
        }
        return entries;
    }

    @Override
    public Map<String, Map<String, Double>> generateEntryStats(Long id) {
        Map<String, Map<String, Double>> responseMap = new HashMap<>();
        DoubleSummaryStatistics weeklyStats;
        DoubleSummaryStatistics monthlyStats;
        DoubleSummaryStatistics yearlyStats;

        IntSummaryStatistics weeklyStatsInt;
        IntSummaryStatistics monthlyStatsInt;
        IntSummaryStatistics yearlyStatsInt;

        Map<String, Double> tempMap;
        Date today = new Date();

        LocalDateTime weekAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(6);
        LocalDateTime monthAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(29);
        LocalDateTime yearAgoLocalDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(364);

        Date weekAgoDate = Date.from(weekAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date monthAgoDate = Date.from(monthAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date yearAgoDate = Date.from(yearAgoLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        List<Entry> weeklyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(weekAgoDate, today, id);
        List<Entry> monthlyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(monthAgoDate, today, id);
        List<Entry> yearlyUserInfo = this.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(yearAgoDate, today, id);

        if (weeklyUserInfo == null || monthlyUserInfo == null || yearlyUserInfo == null) {
            return null;
        }

        weeklyStats = weeklyUserInfo.stream().map(Entry::getWeight).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        monthlyStats = monthlyUserInfo.stream().map(Entry::getWeight).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();
        yearlyStats = yearlyUserInfo.stream().map(Entry::getWeight).map(num -> num == null ? 0 : num).mapToDouble(Double::doubleValue).summaryStatistics();

        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStats.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStats.getAverage() * 100.0) / 100.0);
        responseMap.put("weight", tempMap);

        weeklyStatsInt = weeklyUserInfo.stream().map(Entry::getBloodPressure).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        monthlyStatsInt = monthlyUserInfo.stream().map(Entry::getBloodPressure).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        yearlyStatsInt = yearlyUserInfo.stream().map(Entry::getBloodPressure).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();

        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStatsInt.getAverage() * 100.0) / 100.0);
        responseMap.put("bloodPressure", tempMap);

        weeklyStatsInt = weeklyUserInfo.stream().map(Entry::getGlucose).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        monthlyStatsInt = monthlyUserInfo.stream().map(Entry::getGlucose).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        yearlyStatsInt = yearlyUserInfo.stream().map(Entry::getGlucose).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStatsInt.getAverage() * 100.0) / 100.0);
        responseMap.put("glucose", tempMap);

        weeklyStatsInt = weeklyUserInfo.stream().map(Entry::getInsulin).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        monthlyStatsInt = monthlyUserInfo.stream().map(Entry::getInsulin).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        yearlyStatsInt = yearlyUserInfo.stream().map(Entry::getInsulin).map(num -> num == null ? 0 : num).mapToInt(Integer::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", Math.round(weeklyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("monthly", Math.round(monthlyStatsInt.getAverage() * 100.0) / 100.0);
        tempMap.put("yearly", Math.round(yearlyStatsInt.getAverage() * 100.0) / 100.0);
        responseMap.put("insulin", tempMap);

        weeklyStatsInt = weeklyUserInfo.stream().map(Entry::getHealthy).map(num -> num ? 1 : 0).mapToInt(Integer::valueOf).summaryStatistics();
        monthlyStatsInt = monthlyUserInfo.stream().map(Entry::getHealthy).map(num -> num ? 1 : 0).mapToInt(Integer::valueOf).summaryStatistics();
        yearlyStatsInt = yearlyUserInfo.stream().map(Entry::getHealthy).map(num -> num ? 1 : 0).mapToInt(Integer::valueOf).summaryStatistics();
        tempMap = new HashMap<>();
        tempMap.put("weekly", (double) Math.round(weeklyStatsInt.getAverage() * 100.0));
        tempMap.put("monthly", (double) (Math.round(monthlyStatsInt.getAverage() * 100.0)));
        tempMap.put("yearly", (double) (Math.round(yearlyStatsInt.getAverage() * 100.0)));
        responseMap.put("healthy", tempMap);

        tempMap = new HashMap<>();
        tempMap.put("weekly", (double) weeklyUserInfo.size());
        tempMap.put("monthly", (double) monthlyUserInfo.size());
        tempMap.put("yearly", (double) yearlyUserInfo.size());
        responseMap.put("sizes", tempMap);
        return responseMap;
    }
}
