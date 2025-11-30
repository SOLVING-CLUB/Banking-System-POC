package com.bank.poc.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "System 2 - Core Banking");
        response.put("status", "running");
        response.put("port", 8082);
        response.put("description", "Core banking system for transaction processing, card validation, and PIN authentication");
        response.put("endpoints", Map.of(
            "POST /api/process", "Process transactions",
            "GET /api/transactions/all", "Get all transactions (Admin)",
            "GET /api/transactions/{cardNumber}", "Get customer transactions",
            "GET /api/card/{cardNumber}/balance", "Get card balance"
        ));
        return ResponseEntity.ok(response);
    }
}

