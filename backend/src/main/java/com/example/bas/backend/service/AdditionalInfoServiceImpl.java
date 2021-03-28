package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.repo.AdditionalInfoRepo;
import org.springframework.stereotype.Service;

@Service
public class AdditionalInfoServiceImpl extends BasicServiceImpl<AdditionalInfo, AdditionalInfoRepo, Long> implements AdditionalInfoService {
    public AdditionalInfoServiceImpl(AdditionalInfoRepo additionalInfoRepo) {
        super(additionalInfoRepo);
    }
}
