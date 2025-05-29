package com.tante.landlordtenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/health")
@RestController
@CrossOrigin(origins = "*")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> helloWorld()
    {
        return ResponseEntity.ok("I am alive buddy");
    }
}
