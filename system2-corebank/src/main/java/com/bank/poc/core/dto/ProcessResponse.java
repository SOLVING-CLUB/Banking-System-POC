package com.bank.poc.core.dto;

public class ProcessResponse {
    private String status;  // SUCCESS or FAILED
    private String message;
    private Double newBalance;
    private Long transactionId;
    
    // Builder pattern
    public static ProcessResponseBuilder builder() {
        return new ProcessResponseBuilder();
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
    public static class ProcessResponseBuilder {
        private String status;
        private String message;
        private Double newBalance;
        private Long transactionId;
        
        public ProcessResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public ProcessResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public ProcessResponseBuilder newBalance(Double newBalance) {
            this.newBalance = newBalance;
            return this;
        }
        
        public ProcessResponseBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        
        public ProcessResponse build() {
            ProcessResponse response = new ProcessResponse();
            response.setStatus(status);
            response.setMessage(message);
            response.setNewBalance(newBalance);
            response.setTransactionId(transactionId);
            return response;
        }
    }
}
