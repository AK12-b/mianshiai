package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.ClockInRecord;
import com.example.demo.service.ClockInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clock-in")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClockInController {

    private final ClockInService clockInService;

    @PostMapping("/record")
    public ApiResponse<ClockInRecord> clockIn(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long taskId = Long.valueOf(request.get("taskId").toString());
        Integer taskFinishStatus = Integer.valueOf(request.get("taskFinishStatus").toString());
        Integer trainDuration = request.get("trainDuration") != null ? 
            Integer.valueOf(request.get("trainDuration").toString()) : null;

        ClockInRecord record = clockInService.clockIn(userId, taskId, taskFinishStatus, trainDuration);
        return ApiResponse.success(record);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ClockInRecord>> getUserClockInRecords(@PathVariable Long userId) {
        List<ClockInRecord> records = clockInService.getUserClockInRecords(userId);
        return ApiResponse.success(records);
    }
}