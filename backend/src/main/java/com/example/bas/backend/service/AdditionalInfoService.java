package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AdditionalInfoService extends BasicService<AdditionalInfo, Long> {
    List<AdditionalInfo> findAllByUserId(Long id);

    AdditionalInfo findByUserIdAndEntryDate(Long userId, Date entryDate);

    List<AdditionalInfo> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id);

    List<AdditionalInfo> findAllByEntryDateAndSleepHoursLessThan(Date date, Double sleepHours);

    List<AdditionalInfo> findAllByEntryDate(Date date);

    Map<String, Map<String, Double>> generateAdditionalStats(Long id);
}
