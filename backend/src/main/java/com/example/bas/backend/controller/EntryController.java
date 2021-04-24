package com.example.bas.backend.controller;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.ClassifierEntry;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.service.AdditionalInfoService;
import com.example.bas.backend.service.ClassifierService;
import com.example.bas.backend.service.EntryService;
import com.google.gson.Gson;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/bas/entry")
public class EntryController extends BasicController<EntryService, Entry, Long> {

    private final ClassifierService classifierService;
    private final AdditionalInfoService additionalInfoService;

    public EntryController(EntryService service, ClassifierService classifierService, AdditionalInfoService additionalInfoService) {
        super(service);
        this.classifierService = classifierService;
        this.additionalInfoService = additionalInfoService;
    }

    @Override
    public ResponseEntity<String> save(@Valid @RequestBody final Entry form, Authentication authentication) {

        AppUser user = (AppUser) authentication.getPrincipal();
        if(service.findByEntryDateAndUserId(new Date(),user.getId()) != null){
            return ResponseEntity.status(400).body("You have added an entry today");
        }
        form.setUser(user);
        ClassifierEntry.ClassifierEntryBuilder classifierEntry = ClassifierEntry.builder()
                .Gender(Collections.singletonList(user.getGender()))
                .Glucose(Collections.singletonList(form.getGlucose()))
                .BloodPressure(Collections.singletonList(form.getBloodPressure()))
                .Insulin(Collections.singletonList(form.getInsulin()))
                .BMI(Collections.singletonList(form.getWeight() / (Math.pow((double) user.getHeight() / 100, 2))))
                .Age(Collections.singletonList((int) ((new Date().getTime() - user.getBirthDate().getTime()) / (1000L * 60 * 60 * 24 * 365))));
        List<String> predict = classifierService.predict("tree", new Gson().toJson(classifierEntry).replace("\"", "\\\""), "True");
        int predictionValue = Integer.parseInt(String.valueOf(predict.get(predict.size() - 1).charAt(1)));
        form.setHealthy(predictionValue == 1);
        additionalInfoService.save(AdditionalInfo.builder()
                .user(user)
                .cigarettesAmount(0)
                .sleepHours(0.0)
                .glassesOfWater(0)
                .trainingHours(0.0)
                .alcoholAmount(0)
                .entryDate(new Date())
                .build());
        return super.save(form, authentication);
    }

    @GetMapping(value = "/day", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getEntryFromDay(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate) {
        Entry entry = service.findByEntryDateAndUserId(entryDate, ((AppUser) authentication.getPrincipal()).getId());
        if (entry == null) {
            return ResponseEntity.status(400).body("Item doesn't exist");
        }
        return ResponseEntity.ok(entry);
    }

    @GetMapping(value = "/days", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getEntryBetweenDays(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date stop) {
        List<Entry> entries = service.findAllByEntryDateBetweenAndUserIdOrderByEntryDateDesc(start, stop, ((AppUser) authentication.getPrincipal()).getId());
        if (entries == null) {
            return ResponseEntity.status(400).body("Item doesn't exist");
        }
        return ResponseEntity.ok(entries);
    }
}
