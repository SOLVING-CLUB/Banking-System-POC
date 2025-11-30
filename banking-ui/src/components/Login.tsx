import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
} from '@mui/material';
import type { User } from '../types';

interface LoginProps {
  onLogin: (user: User) => void;
}

export default function Login({ onLogin }: LoginProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // Hardcoded credentials for POC
    if (username === 'cust1' && password === 'pass') {
      const user: User = { username: 'cust1', role: 'CUSTOMER' };
      localStorage.setItem('user', JSON.stringify(user));
      onLogin(user);
      navigate('/customer/dashboard');
    } else if (username === 'admin' && password === 'admin') {
      const user: User = { username: 'admin', role: 'ADMIN' };
      localStorage.setItem('user', JSON.stringify(user));
      onLogin(user);
      navigate('/admin/dashboard');
    } else {
      setError('Invalid credentials');
    }
  };

  return (
    <Container maxWidth="sm" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
      <Box sx={{ width: '100%', marginTop: 8 }}>
        <Paper elevation={3} sx={{ padding: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom align="center">
            Banking System Login
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Username"
              variant="outlined"
              margin="normal"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {error && (
              <Alert severity="error" sx={{ marginTop: 2 }}>
                {error}
              </Alert>
            )}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ marginTop: 3 }}
            >
              Login
            </Button>
          </form>
          <Box sx={{ marginTop: 3 }}>
            <Typography variant="body2" color="text.secondary">
              Customer: cust1 / pass
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Admin: admin / admin
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}

