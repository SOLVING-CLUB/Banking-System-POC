package com.bank.poc.gateway.service;

import com.bank.poc.gateway.dto.TransactionRequest;
import com.bank.poc.gateway.dto.TransactionResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {
    
    private final RestTemplate restTemplate;
    
    // Default URLs - localhost for local dev, production for deployment
    private static final String DEFAULT_LOCAL_URL = "http://localhost:8082";
    private static final String DEFAULT_PRODUCTION_URL = "https://system2-corebank-87wm.onrender.com";
    
    @Value("${system2.url:http://localhost:8082}")
    private String system2Url;
    
    private String finalSystem2Url;
    
    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @PostConstruct
    public void init() {
        // Use property value if available, otherwise use localhost default
        String url = (system2Url != null && !system2Url.trim().isEmpty()) 
            ? system2Url.trim() 
            : DEFAULT_LOCAL_URL;
        
        // Remove trailing slash if present
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        
        // Validate URL format
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            System.err.println("WARNING: System 2 URL missing protocol, using localhost default: " + DEFAULT_LOCAL_URL);
            url = DEFAULT_LOCAL_URL;
        }
        
        finalSystem2Url = url;
        System.out.println("System 2 URL configured as: '" + finalSystem2Url + "'");
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
            // Use the validated URL from init()
            String baseUrl = finalSystem2Url != null ? finalSystem2Url : DEFAULT_LOCAL_URL;
            
            // Ensure it's a valid absolute URL
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                System.err.println("ERROR: Invalid URL format, using localhost default: " + DEFAULT_LOCAL_URL);
                baseUrl = DEFAULT_LOCAL_URL;
            }
            
            // Remove trailing slash if present
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            
            String url = baseUrl + "/api/process";
            System.out.println("Attempting to connect to System 2 at: " + url);
            System.out.println("Base URL: " + baseUrl);
            
            // Final validation - ensure URL is absolute
            if (url == null || url.trim().isEmpty() || (!url.startsWith("http://") && !url.startsWith("https://"))) {
                throw new IllegalArgumentException("Invalid URL format: " + url);
            }
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

