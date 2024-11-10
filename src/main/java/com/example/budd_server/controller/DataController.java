package com.example.budd_server.controller;

import com.example.budd_server.dto.CommentProjection;
import com.example.budd_server.service.ResponseService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://budd-client.vercel.app", "http://localhost:5173", "https://budd-report.vercel.app/"})
@RestController
@RequestMapping("/api")
public class DataController {
    @Autowired
    private ResponseService responseService;

    @GetMapping("/comments")
    public ResponseEntity<Page<CommentProjection>> getAllCommentsByPage(
            @RequestParam(defaultValue = "1") int page) {
        Page<CommentProjection> commentsPage = responseService.getAllCommentsByPage(page - 1);
        return ResponseEntity.ok(commentsPage);
    }
}
