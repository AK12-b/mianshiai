package com.example.demo.dto;

import lombok.Data;

@Data
public class QwenRequest {
    private String model;
    private Message[] messages;
    private Boolean stream;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}