package com.bank.poc.gateway.dto;

public class TransactionResponse {
    private String status;  // SUCCESS or FAILED
    private String message;
    private Double newBalance;
    private Long transactionId;
    
    // Builder pattern
    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Double getNewBalance() {
        return newBalance;
    }
    
    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }
    
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    // Builder class
    public static class TransactionResponseBuilder {
        private String status;
        private String message;
        private Double newBalance;
        private Long transactionId;
        
        public TransactionResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public TransactionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public TransactionResponseBuilder newBalance(Double newBalance) {
            this.newBalance = newBalance;
            return this;
        }
        
        public TransactionResponseBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        
        public TransactionResponse build() {
            TransactionResponse response = new TransactionResponse();
            response.setStatus(status);
            response.setMessage(message);
            response.setNewBalance(newBalance);
            response.setTransactionId(transactionId);
            return response;
        }
    }
}
