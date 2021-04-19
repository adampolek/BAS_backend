package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.repo.AdditionalInfoRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdditionalInfoServiceImpl extends BasicServiceImpl<AdditionalInfo, AdditionalInfoRepo, Long> implements AdditionalInfoService {
    public AdditionalInfoServiceImpl(AdditionalInfoRepo additionalInfoRepo) {
        super(additionalInfoRepo);
    }

    @Override
    public List<AdditionalInfo> findAllByUserId(Long id) {
        List<AdditionalInfo> info = new ArrayList<>();
        try {
            info = repo.findAllByUserId(id).orElseThrow(() -> new UsernameNotFoundException("User with that id doesn't exist"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
