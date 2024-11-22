package com.example.budd_server.service;

import com.example.budd_server.dto.CommentProjection;
import com.example.budd_server.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    // 전체 comment 목록을 페이지네이션을 적용하여 가져오는 메소드
    public Page<CommentProjection> getAllCommentsByPage(int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date"));  // 최신순으로 정렬
        Page<CommentProjection> commentsPage = responseRepository.findByCommentIsNotNull(pageable);

        // 현재 페이지가 전체 페이지 수를 초과하는 경우
        if (page >= commentsPage.getTotalPages()) {
            throw new IllegalArgumentException("더 이상 페이지가 존재하지 않습니다.");
        }

        return commentsPage;
    }
}