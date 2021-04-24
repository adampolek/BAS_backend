package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.repo.AdditionalInfoRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdditionalInfoServiceImpl extends BasicServiceImpl<AdditionalInfo, AdditionalInfoRepo, Long> implements AdditionalInfoService {
    public AdditionalInfoServiceImpl(AdditionalInfoRepo additionalInfoRepo) {
        super(additionalInfoRepo);
    }

    @Override
    public List<AdditionalInfo> findAllByUserId(Long id) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("User with that id doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public AdditionalInfo findByUserIdAndEntryDate(Long userId, Date entryDate) {
        AdditionalInfo info = null;
        try {
            info = repo.findByUserIdAndEntryDate(userId, entryDate).orElseThrow(() -> new UsernameNotFoundException("Additional info doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public List<AdditionalInfo> findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(Date start, Date stop, Long id) {
        List<AdditionalInfo> info = null;
        try {
            info = repo.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, id).orElseThrow(() -> new UsernameNotFoundException("Additional info doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
