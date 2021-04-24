package com.example.bas.backend.controller;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AdditionalInfoForm;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.service.AdditionalInfoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/bas/additional_info")
public class AdditionalInfoController extends BasicController<AdditionalInfoService, AdditionalInfo, Long> {

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
}
