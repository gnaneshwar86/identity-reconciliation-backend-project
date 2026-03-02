package com.gnaneshwar.identity_reconciliation.controller;

import com.gnaneshwar.identity_reconciliation.dto.*;
import com.gnaneshwar.identity_reconciliation.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
@RequiredArgsConstructor
public class IdentifyController {

    private final IdentityService service;

    @PostMapping
    public IdentifyResponse identify(@RequestBody IdentifyRequest request) {
        return service.identify(request);
    }
}