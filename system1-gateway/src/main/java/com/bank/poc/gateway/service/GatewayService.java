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
        // Trim and validate the URL
        if (system2Url != null) {
            system2Url = system2Url.trim();
            // Remove trailing slash if present
            if (system2Url.endsWith("/")) {
                system2Url = system2Url.substring(0, system2Url.length() - 1);
            }
        }
        System.out.println("System 2 URL configured as: " + system2Url);
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
            // Ensure URL is properly formatted
            String baseUrl = system2Url != null ? system2Url.trim() : "";
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String url = baseUrl + "/api/process";
            System.out.println("Attempting to connect to System 2 at: " + url);
            System.out.println("Base URL: " + baseUrl);
            ResponseEntity<TransactionResponse> response = restTemplate.postForEntity(
                    url, 
                    request, 
                    TransactionResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("Successfully connected to System 2");
                return response.getBody();
            } else {
                System.err.println("System 2 returned status: " + response.getStatusCode());
                return TransactionResponse.builder()
                        .status("FAILED")
                        .message("System 2 processing error")
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Error routing to System 2: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return TransactionResponse.builder()
                    .status("FAILED")
                    .message("Failed to connect to processing system: " + e.getMessage())
                    .build();
        }
    }
}

