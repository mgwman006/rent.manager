package com.tante.landlordtenant.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class UrlRedirectController {

    @GetMapping("/")
    public ResponseEntity<Void> redirect() {
        String amplifyUrl = "https://www.tante.tz/";
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", amplifyUrl).build();
    }

}
