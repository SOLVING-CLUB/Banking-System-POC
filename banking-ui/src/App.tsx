import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import ProtectedRoute from './components/ProtectedRoute';
import CustomerDashboard from './pages/CustomerDashboard';
import AdminDashboard from './pages/AdminDashboard';
import type { User } from './types';
import './App.css';

function App() {
  const [_user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const checkUser = () => {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        try {
          setUser(JSON.parse(userStr));
        } catch (e) {
          localStorage.removeItem('user');
          setUser(null);
        }
      } else {
        setUser(null);
      }
    };

    // Check user on mount
    checkUser();

    // Listen for storage changes (logout from other tabs, etc.)
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'user') {
        checkUser();
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const handleLogin = (loggedInUser: User) => {
    setUser(loggedInUser);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <div className="app-container">
      <BrowserRouter>
        <Routes>
        <Route
          path="/login"
          element={
            (() => {
              const userStr = localStorage.getItem('user');
              if (userStr) {
                try {
                  const storedUser = JSON.parse(userStr);
                  return (
                    <Navigate
                      to={storedUser.role === 'ADMIN' ? '/admin/dashboard' : '/customer/dashboard'}
                      replace
                    />
                  );
                } catch (e) {
                  localStorage.removeItem('user');
                  return <Login onLogin={handleLogin} />;
                }
              }
              return <Login onLogin={handleLogin} />;
            })()
          }
        />
        <Route
          path="/customer/dashboard"
          element={
            <ProtectedRoute requiredRole="CUSTOMER">
              <CustomerDashboard onLogout={handleLogout} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminDashboard onLogout={handleLogout} />
            </ProtectedRoute>
          }
        />
        <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
