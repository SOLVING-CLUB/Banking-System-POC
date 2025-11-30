import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Alert,
  CircularProgress,
  Card,
  CardContent,
} from '@mui/material';
import type { Transaction, TransactionRequest } from '../types';
import { transactionService } from '../services/api';

const DEFAULT_CARD = '4123456789012345';

interface CustomerDashboardProps {
  onLogout?: () => void;
}

export default function CustomerDashboard({ onLogout }: CustomerDashboardProps) {
  const navigate = useNavigate();
  const [balance, setBalance] = useState<number | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [topupAmount, setTopupAmount] = useState('');
  const [pin, setPin] = useState('');
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [balanceData, transactionsData] = await Promise.all([
        transactionService.getCardBalance(DEFAULT_CARD),
        transactionService.getCustomerTransactions(DEFAULT_CARD),
      ]);
      setBalance(balanceData);
      setTransactions(transactionsData);
    } catch (error) {
      console.error('Error loading data:', error);
      setMessage({ type: 'error', text: 'Failed to load data' });
    } finally {
      setLoading(false);
    }
  };

  const handleTopup = async (e: React.FormEvent) => {
    e.preventDefault();
    setProcessing(true);
    setMessage(null);

    try {
      const request: TransactionRequest = {
        cardNumber: DEFAULT_CARD,
        pin: pin,
        amount: parseFloat(topupAmount),
        type: 'topup',
      };

      const response = await transactionService.processTransaction(request);

      if (response.status === 'SUCCESS') {
        setMessage({ type: 'success', text: response.message });
        setTopupAmount('');
        setPin('');
        await loadData(); // Refresh data
      } else {
        setMessage({ type: 'error', text: response.message });
      }
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Transaction failed' });
    } finally {
      setProcessing(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    if (onLogout) {
      onLogout();
    }
    navigate('/login', { replace: true });
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
      <Box sx={{ width: '100%', maxWidth: '1200px', marginTop: 4, marginBottom: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 3, width: '100%' }}>
          <Typography variant="h4">Customer Dashboard</Typography>
          <Button variant="outlined" onClick={handleLogout}>
            Logout
          </Button>
        </Box>

        <Card sx={{ marginBottom: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Current Balance
            </Typography>
            <Typography variant="h4" color="primary">
              ${balance?.toFixed(2) || '0.00'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Card: {DEFAULT_CARD}
            </Typography>
          </CardContent>
        </Card>

        <Paper elevation={3} sx={{ padding: 3, marginBottom: 3 }}>
          <Typography variant="h6" gutterBottom>
            Top-Up Account
          </Typography>
          <form onSubmit={handleTopup}>
            <TextField
              fullWidth
              label="Amount"
              type="number"
              variant="outlined"
              margin="normal"
              value={topupAmount}
              onChange={(e) => setTopupAmount(e.target.value)}
              required
              inputProps={{ min: 0.01, step: 0.01 }}
            />
            <TextField
              fullWidth
              label="PIN"
              type="password"
              variant="outlined"
              margin="normal"
              value={pin}
              onChange={(e) => setPin(e.target.value)}
              required
            />
            <Button
              type="submit"
              variant="contained"
              fullWidth
              sx={{ marginTop: 2 }}
              disabled={processing}
            >
              {processing ? 'Processing...' : 'Top Up'}
            </Button>
          </form>
          {message && (
            <Alert severity={message.type} sx={{ marginTop: 2 }}>
              {message.text}
            </Alert>
          )}
        </Paper>

        <Paper elevation={3} sx={{ padding: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 2 }}>
            <Typography variant="h6">Transaction History</Typography>
            <Button variant="outlined" onClick={loadData}>
              Refresh
            </Button>
          </Box>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Amount</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Reason</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {transactions.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      No transactions found
                    </TableCell>
                  </TableRow>
                ) : (
                  transactions.map((tx) => (
                    <TableRow key={tx.id}>
                      <TableCell>
                        {new Date(tx.timestamp).toLocaleString()}
                      </TableCell>
                      <TableCell>{tx.type.toUpperCase()}</TableCell>
                      <TableCell>${tx.amount.toFixed(2)}</TableCell>
                      <TableCell>
                        <Typography
                          color={tx.status === 'SUCCESS' ? 'success.main' : 'error.main'}
                        >
                          {tx.status}
                        </Typography>
                      </TableCell>
                      <TableCell>{tx.reason || '-'}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>
    </Container>
  );
}

