package com.example.budd_server.service;

import com.example.budd_server.entity.Response;
import com.example.budd_server.repository.ModalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModalService {
    @Autowired
    ModalRepository modalRepository;

    public List<Response> getResponse(int userId) {
        List<Response> responses = modalRepository.findByUserId(userId);
        System.out.println("Retrieved responses from repository: " + responses);
        return responses;

    }
}
