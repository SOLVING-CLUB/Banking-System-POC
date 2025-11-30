package com.bank.poc.gateway.controller;

import com.bank.poc.gateway.dto.TransactionRequest;
import com.bank.poc.gateway.dto.TransactionResponse;
import com.bank.poc.gateway.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final GatewayService gatewayService;
    
    public TransactionController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }
    
    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> handleTransaction(@RequestBody TransactionRequest request) {
        // Validate request
        String validationError = gatewayService.validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    TransactionResponse.builder()
                            .status("FAILED")
                            .message(validationError)
                            .build()
            );
        }
        
        // Route to System 2
        TransactionResponse response = gatewayService.routeTransaction(request);
        return ResponseEntity.ok(response);
    }
}

