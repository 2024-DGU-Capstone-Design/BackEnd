package com.example.budd_server.controller;

import com.example.budd_server.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") //cors 문제 해결
@RestController
@RequestMapping("/api")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;

    @PostMapping("/call")
    public String makeCall(@RequestParam String to) {
        return twilioService.startCallFlow(to);
    }
}
