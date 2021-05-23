package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.repo.AdditionalInfoRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
}
