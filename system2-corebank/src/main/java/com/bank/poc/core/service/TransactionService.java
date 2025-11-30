package com.bank.poc.core.service;

import com.bank.poc.core.dto.ProcessRequest;
import com.bank.poc.core.dto.ProcessResponse;
import com.bank.poc.core.entity.Card;
import com.bank.poc.core.entity.Transaction;
import com.bank.poc.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {
    
    private final CardService cardService;
    private final TransactionRepository transactionRepository;
    
    public TransactionService(CardService cardService, TransactionRepository transactionRepository) {
        this.cardService = cardService;
        this.transactionRepository = transactionRepository;
    }
    
    @Transactional
    public ProcessResponse processTransaction(ProcessRequest request) {
        // Find card
        Optional<Card> cardOpt = cardService.findByCardNumber(request.getCardNumber());
        
        if (cardOpt.isEmpty()) {
            return createFailedResponse("Invalid card", null);
        }
        
        Card card = cardOpt.get();
        
        // Validate PIN
        if (!cardService.validatePin(card, request.getPin())) {
            Transaction failedTx = createTransaction(card.getCardNumber(), request, "FAILED", "Invalid PIN");
            transactionRepository.save(failedTx);
            return createFailedResponse("Invalid PIN", card.getBalance());
        }
        
        // Check balance for withdrawals
        if ("withdraw".equals(request.getType())) {
            if (card.getBalance() < request.getAmount()) {
                Transaction failedTx = createTransaction(card.getCardNumber(), request, "FAILED", "Insufficient balance");
                transactionRepository.save(failedTx);
                return createFailedResponse("Insufficient balance", card.getBalance());
            }
        }
        
        // Process transaction
        cardService.updateBalance(card, request.getAmount(), request.getType());
        
        // Save transaction record
        Transaction transaction = createTransaction(card.getCardNumber(), request, "SUCCESS", null);
        transactionRepository.save(transaction);
        
        return ProcessResponse.builder()
                .status("SUCCESS")
                .message("Transaction processed successfully")
                .newBalance(card.getBalance())
                .transactionId(transaction.getId())
                .build();
    }
    
    private Transaction createTransaction(String cardNumber, ProcessRequest request, String status, String reason) {
        Transaction transaction = new Transaction();
        transaction.setCardNumber(cardNumber);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(status);
        transaction.setReason(reason);
        return transaction;
    }
    
    private ProcessResponse createFailedResponse(String message, Double balance) {
        return ProcessResponse.builder()
                .status("FAILED")
                .message(message)
                .newBalance(balance)
                .build();
    }
}

