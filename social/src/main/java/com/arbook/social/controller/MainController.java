package com.arbook.social.controller;


import jdk.jfr.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class MainController {


    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from our API!");
    }

    @GetMapping("/say-goodbye")
    public ResponseEntity<String> sayGoodbye() {
        return ResponseEntity.ok("Goodbye and see you later!");
    }
}
