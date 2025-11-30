package com.bank.poc.core.config;

import com.bank.poc.core.entity.Card;
import com.bank.poc.core.repository.CardRepository;
import com.bank.poc.core.service.CardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final CardRepository cardRepository;
    private final CardService cardService;
    
    public DataInitializer(CardRepository cardRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }
    
    @Override
    public void run(String... args) {
        // Seed initial test card
        if (cardRepository.findByCardNumber("4123456789012345").isEmpty()) {
            Card card = new Card();
            card.setCardNumber("4123456789012345");
            card.setPinHash(cardService.hashPin("1234"));  // PIN: 1234
            card.setBalance(1000.00);
            card.setCustomerName("John Doe");
            
            cardRepository.save(card);
            System.out.println("Initial test card seeded: 4123456789012345 with balance 1000.00");
        }
    }
}

