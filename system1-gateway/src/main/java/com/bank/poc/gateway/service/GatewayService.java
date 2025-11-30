package com.bank.poc.gateway.service;

import com.bank.poc.gateway.dto.TransactionRequest;
import com.bank.poc.gateway.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {
    
    private final RestTemplate restTemplate;
    
    @Value("${system2.url}")
    private String system2Url;
    
    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Validate transaction request
     */
    public String validateRequest(TransactionRequest request) {
        // Check required fields
        if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
            return "Card number is required";
        }
        if (request.getPin() == null || request.getPin().trim().isEmpty()) {
            return "PIN is required";
        }
        if (request.getAmount() <= 0) {
            return "Amount must be greater than 0";
        }
        if (request.getType() == null || 
            (!request.getType().equals("withdraw") && !request.getType().equals("topup"))) {
            return "Type must be either 'withdraw' or 'topup'";
        }
        return null;  // Validation passed
    }
    
    /**
     * Route transaction to System 2 based on card range
     */
    public TransactionResponse routeTransaction(TransactionRequest request) {
        // Check card range - only cards starting with '4' are supported
        if (!request.getCardNumber().startsWith("4")) {
            return TransactionResponse.builder()
                    .status("FAILED")
                    .message("Card range not supported")
                    .build();
        }
        
        // Forward to System 2
        try {
            String url = system2Url + "/api/process";
            ResponseEntity<TransactionResponse> response = restTemplate.postForEntity(
                    url, 
                    request, 
                    TransactionResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                return TransactionResponse.builder()
                        .status("FAILED")
                        .message("System 2 processing error")
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Error routing to System 2: " + e.getMessage());
            return TransactionResponse.builder()
                    .status("FAILED")
                    .message("Failed to connect to processing system")
                    .build();
        }
    }
}

