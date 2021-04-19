package com.example.bas.backend.service;

import com.example.bas.backend.model.AdditionalInfo;

import java.util.List;

public interface AdditionalInfoService extends BasicService<AdditionalInfo, Long>{
    List<AdditionalInfo> findAllByUserId(Long id);
}
