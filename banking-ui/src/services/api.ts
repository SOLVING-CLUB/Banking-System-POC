import axios from 'axios';
import type { TransactionRequest, TransactionResponse, Transaction } from '../types';

// Hardcoded production API URLs for Render deployment
const API_BASE_URL = 'https://system1-gateway.onrender.com/api';
const SYSTEM2_BASE_URL = 'https://system2-corebank-87wm.onrender.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const system2Api = axios.create({
  baseURL: SYSTEM2_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const transactionService = {
  // Process transaction through System 1 (Gateway)
  processTransaction: async (request: TransactionRequest): Promise<TransactionResponse> => {
    const response = await api.post<TransactionResponse>('/transaction', request);
    return response.data;
  },
  
  // Get all transactions (for Admin)
  getAllTransactions: async (): Promise<Transaction[]> => {
    const response = await system2Api.get<Transaction[]>('/transactions/all');
    return response.data;
  },
  
  // Get transactions for a specific card (for Customer)
  getCustomerTransactions: async (cardNumber: string): Promise<Transaction[]> => {
    const response = await system2Api.get<Transaction[]>(`/transactions/${cardNumber}`);
    return response.data;
  },
  
  // Get card balance
  getCardBalance: async (cardNumber: string): Promise<number> => {
    const response = await system2Api.get<{ balance: number }>(`/card/${cardNumber}/balance`);
    return response.data.balance;
  },
};

