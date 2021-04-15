package com.example.bas.backend.controller;

import com.example.bas.backend.service.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
public class BasicController<CLASS_SERVICE extends BasicService<CLASS, ID>, CLASS, ID> {

    protected final CLASS_SERVICE service;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<List<CLASS>> all() {
        return ResponseEntity.status(200).body(service.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<CLASS> findById(@PathVariable final ID id) {
        return ResponseEntity.status(200).body(service.findById(id));
    }

    @PostMapping(value = "/save")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> save(@Valid @RequestBody final CLASS form, Authentication authentication) {
        return service.save(form) ? ResponseEntity.status(201).body("Successfully add " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " added");
    }

    @PutMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> update(@Valid @RequestBody final CLASS form, Authentication authentication) {
        return service.save(form) ? ResponseEntity.status(201).body("Successfully update " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " updated");
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable final ID id) {
        return service.deleteById(id) ? ResponseEntity.status(200).body("Successfully delete " + getClass().getName()) : ResponseEntity.badRequest().body("No " + getClass().getName() + " deleted");
    }

}