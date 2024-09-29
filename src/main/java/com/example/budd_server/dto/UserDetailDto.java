package com.example.budd_server.dto;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private User user;
    private List<CallRecord> callRecords;
    private List<Response> responses;
    private List<Report> reports;
}
