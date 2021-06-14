package com.example.bas.backend.controller;

import com.example.bas.backend.model.AdditionalInfo;
import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.model.forms.ClassifierEntry;
import com.example.bas.backend.service.AdditionalInfoService;
import com.example.bas.backend.service.ClassifierService;
import com.example.bas.backend.service.EmailService;
import com.example.bas.backend.service.EntryService;
import com.google.gson.Gson;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/bas/entry")
public class EntryController extends BasicController<EntryService, Entry, Long> {

    private static final Logger logger = Logger.getLogger(EntryController.class.getName());
    private final ClassifierService classifierService;
    private final AdditionalInfoService additionalInfoService;
    private final EmailService emailService;

    public EntryController(EntryService service, ClassifierService classifierService, AdditionalInfoService additionalInfoService, EmailService emailService) {
        super(service);
        this.classifierService = classifierService;
        this.additionalInfoService = additionalInfoService;
        this.emailService = emailService;
    }

    @Override
    public ResponseEntity<String> save(@Valid @RequestBody final Entry form, Authentication authentication) {

        AppUser user = (AppUser) authentication.getPrincipal();
        if (service.findByEntryDateAndUserId(new Date(), user.getId()) != null) {
            return ResponseEntity.status(400).body("You have added an entry today");
        }
        form.setUser(user);
        ClassifierEntry classifierEntry = ClassifierEntry.builder()
                .Gender(Collections.singletonList(user.getGender()))
                .Glucose(Collections.singletonList(form.getGlucose()))
                .BloodPressure(Collections.singletonList(form.getBloodPressure()))
                .Insulin(Collections.singletonList(form.getInsulin()))
                .BMI(Collections.singletonList(form.getWeight() / (Math.pow((double) user.getHeight() / 100, 2))))
                .Age(Collections.singletonList((int) ((new Date().getTime() - user.getBirthDate().getTime()) / (1000L * 60 * 60 * 24 * 365)))).build();
        List<String> predict = classifierService.predict(new Gson().toJson(classifierEntry)
//                .replace("\"", "\\\"")//zakomentować linię przed wrzuceniem na serwer
        );
        if (predict.isEmpty()) {
            logger.warning("There was an error with your classifier.");
        } else {
            int predictionValue = Integer.parseInt(String.valueOf(predict.get(predict.size() - 1).charAt(1)));
            form.setHealthy(predictionValue == 0);
            if (predictionValue == 1) {
                emailService.send(user.getEmail(), "Your last results - warning", "Your last entry " +
                        "suggests that you might be sick!");
            }
        }
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

    @GetMapping(value = "/isEntry", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> isEntryDateSet(
            Authentication authentication,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate) {
        return ResponseEntity.ok(service.isEntrySet(entryDate, ((AppUser) authentication.getPrincipal()).getId()));
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

    @GetMapping(value = "/train", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> train(Authentication authentication) {
        List<String> errors = classifierService.train();
        return ResponseEntity.ok(errors);
    }

    @GetMapping(value = "/set_clf", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> setClassifier(Authentication authentication, @RequestParam String clf) {
        classifierService.setClassifier(clf);
        return ResponseEntity.ok("Classifier set to " + clf);
    }

    @GetMapping(value = "/get_clf", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getClassifier(Authentication authentication) {
        return ResponseEntity.ok(classifierService.getClassifier());
    }

    @GetMapping(value = "/get_all_clf", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllClassifiers(Authentication authentication) {
        List<String> classifiers = new ArrayList<>(Arrays.asList("tree", "knn", "net"));
        return ResponseEntity.ok(classifiers);
    }

    @GetMapping(value = "/get_csv", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCSV(Authentication authentication) {
        return ResponseEntity.ok(classifierService.getCSV());
    }

    @GetMapping(value = "/send_stats", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> sendStats(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        emailService.sendStats(user.getEmail(), user.getUsername());
        return ResponseEntity.ok("Email sent");
    }
}
