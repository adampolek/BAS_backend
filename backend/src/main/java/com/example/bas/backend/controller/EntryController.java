package com.example.bas.backend.controller;

import com.example.bas.backend.model.Entry;
import com.example.bas.backend.service.EntryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bas/entry")
public class EntryController extends BasicController<EntryService, Entry,Long>{
    public EntryController(EntryService service) {
        super(service);
    }
}
