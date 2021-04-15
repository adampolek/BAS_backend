package com.example.bas.backend.controller;

import com.example.bas.backend.model.AppUser;
import com.example.bas.backend.model.Entry;
import com.example.bas.backend.service.EntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/bas/entry")
public class EntryController extends BasicController<EntryService, Entry,Long>{
    public EntryController(EntryService service) {
        super(service);
    }

    @Override
    public ResponseEntity<String> save(@Valid @RequestBody final Entry form, Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        form.setUser(user);
        return super.save(form, authentication);
    }
}
