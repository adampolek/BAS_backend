package com.example.bas.backend.controller;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.ClassifierEntry;
import com.example.bas.backend.model.Entry;
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

    public EntryController(EntryService service, ClassifierService classifierService) {
        super(service);
        this.classifierService = classifierService;
    }

    @Override
    public ResponseEntity<String> save(@Valid @RequestBody final Entry form, Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
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
        return super.save(form, authentication);
    }

    @GetMapping(value = "/entry", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getEntryFromDay(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate) {
        return ResponseEntity.ok(new Gson().toJson(service.findByEntryDate(entryDate)));
    }

    @GetMapping(value = "/entry_days", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getEntryBetweenDays(Authentication authentication, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date stop) {
        return ResponseEntity.ok(new Gson().toJson(service.findAllByEntryDateBetween(start, stop)));
    }
}
