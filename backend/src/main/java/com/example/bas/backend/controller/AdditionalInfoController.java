package com.example.bas.backend.controller;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.service.AdditionalInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bas/additional_info")
public class AdditionalInfoController extends BasicController<AdditionalInfoService, AdditionalInfo, Long> {
    public AdditionalInfoController(AdditionalInfoService service) {
        super(service);
    }
}
