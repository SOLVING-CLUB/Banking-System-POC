package com.bank.poc.core.controller;

import com.bank.poc.core.dto.ProcessRequest;
import com.bank.poc.core.dto.ProcessResponse;
import com.bank.poc.core.entity.Card;
import com.bank.poc.core.entity.Transaction;
import com.bank.poc.core.repository.CardRepository;
import com.bank.poc.core.repository.TransactionRepository;
import com.bank.poc.core.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProcessingController {
    
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    
    public ProcessingController(TransactionService transactionService, 
                                TransactionRepository transactionRepository,
                                CardRepository cardRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }
    
    @PostMapping("/process")
    public ResponseEntity<ProcessResponse> processTransaction(@RequestBody ProcessRequest request) {
        ProcessResponse response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/transactions/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions/{cardNumber}")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(@PathVariable String cardNumber) {
        List<Transaction> transactions = transactionRepository.findByCardNumberOrderByTimestampDesc(cardNumber);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/card/{cardNumber}/balance")
    public ResponseEntity<Map<String, Object>> getCardBalance(@PathVariable String cardNumber) {
        Optional<Card> cardOpt = cardRepository.findByCardNumber(cardNumber);
        if (cardOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Card not found");
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("balance", cardOpt.get().getBalance());
        return ResponseEntity.ok(response);
    }
}

