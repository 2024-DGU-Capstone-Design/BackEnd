package com.example.budd_server.controller;

import com.example.budd_server.entity.Response;
import com.example.budd_server.service.ModalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ModalController {
    @Autowired
    private ModalService modalService;

    @GetMapping("/call")
            public ResponseEntity<List<Response>> getResponse(@RequestParam int userId){
            List<Response> responses = modalService.getResponse(userId);

            return ResponseEntity.ok(responses);
    }

}
