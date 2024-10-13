package com.example.budd_server.controller;

import com.example.budd_server.service.CallService;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestCallController {

    @Autowired
    private CallService callService;

    @GetMapping("/test/call")
    public String makeTestCall(@RequestParam("to") String to) {
        try {
            Call call = callService.makeCall(to);
            System.out.println("Call SID: " + call.getSid());
            return "Call made successfully! Call SID: " + call.getSid();  // 성공
        } catch (ApiException e) {
            System.err.println("API Exception: " + e.getMessage());
            return "API Exception occurred: " + e.getMessage();  // API 예외 발생
        } catch (Exception e) {
            System.err.println("General Exception: " + e.getMessage());
            return "General Exception occurred: " + e.getMessage();  // 일반 예외 발생
        }
    }
}
