export type UserRole = 'CUSTOMER' | 'ADMIN';

export interface User {
  username: string;
  role: UserRole;
}

export interface Transaction {
  id: number;
  cardNumber: string;
  type: 'withdraw' | 'topup';
  amount: number;
  timestamp: string;
  status: 'SUCCESS' | 'FAILED';
  reason?: string;
}

export interface Card {
  cardNumber: string;
  balance: number;
  customerName: string;
}

export interface TransactionRequest {
  cardNumber: string;
  pin: string;
  amount: number;
  type: 'withdraw' | 'topup';
}

export interface TransactionResponse {
  status: 'SUCCESS' | 'FAILED';
  message: string;
  newBalance?: number;
  transactionId?: number;
}

