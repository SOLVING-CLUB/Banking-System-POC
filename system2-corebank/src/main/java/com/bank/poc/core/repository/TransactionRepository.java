package com.bank.poc.core.repository;

import com.bank.poc.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCardNumberOrderByTimestampDesc(String cardNumber);
    List<Transaction> findAllByOrderByTimestampDesc();
}

