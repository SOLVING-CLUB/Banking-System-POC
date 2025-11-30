package com.bank.poc.core.service;

import com.bank.poc.core.entity.Card;
import com.bank.poc.core.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class CardService {
    
    private final CardRepository cardRepository;
    
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    
    /**
     * Hash PIN using SHA-256
     * NEVER store or log plain-text PINs
     */
    public String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    /**
     * Find card by card number
     */
    public Optional<Card> findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }
    
    /**
     * Validate PIN by comparing hashed input with stored hash
     */
    public boolean validatePin(Card card, String inputPin) {
        String inputPinHash = hashPin(inputPin);
        return card.getPinHash().equals(inputPinHash);
    }
    
    /**
     * Update card balance
     */
    public void updateBalance(Card card, double amount, String type) {
        if ("withdraw".equals(type)) {
            card.setBalance(card.getBalance() - amount);
        } else if ("topup".equals(type)) {
            card.setBalance(card.getBalance() + amount);
        }
        cardRepository.save(card);
    }
}

