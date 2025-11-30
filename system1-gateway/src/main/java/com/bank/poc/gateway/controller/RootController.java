package com.bank.poc.gateway.controller;

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
        response.put("service", "System 1 - Gateway API");
        response.put("status", "running");
        response.put("port", 8081);
        response.put("description", "Gateway API for transaction routing and validation");
        response.put("endpoints", Map.of(
            "POST /api/transaction", "Process transaction (validates and routes to System 2)"
        ));
        response.put("cardRange", "Only cards starting with '4' are supported");
        return ResponseEntity.ok(response);
    }
}

