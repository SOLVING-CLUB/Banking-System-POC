# Banking System POC

A simple banking system demo with two backend services and a web interface. Users can perform transactions, view balances, and admins can monitor all activity.

## 🌐 Live Deployment

**Frontend Application**: [https://banking-ui-eryw.onrender.com](https://banking-ui-eryw.onrender.com)

**Backend Services**:
- **System 1 (Gateway)**: [https://system1-gateway.onrender.com](https://system1-gateway.onrender.com)
- **System 2 (Core Banking)**: [https://system2-corebank-87wm.onrender.com](https://system2-corebank-87wm.onrender.com)

**Note**: Free tier services may take 30-60 seconds to wake up if they've been idle for more than 15 minutes.

## What This Does

- **System 1 (Gateway)**: Validates and routes transactions (port 8081)
- **System 2 (Core Banking)**: Processes transactions, validates PINs, manages balances (port 8082)
- **Frontend**: Web interface for customers and admins (port 5173)

## What You Need

- Java 17 or higher
- Maven (for Java projects)
- Node.js 18+ and npm
- A web browser

## Quick Start

### Step 1: Start System 2 (Core Banking)

Open a terminal and run:

```bash
cd system2-corebank
mvn spring-boot:run
```

Wait until you see "Started CoreBankApplication" - this means it's running on **http://localhost:8082**

### Step 2: Start System 1 (Gateway)

Open a **new terminal** and run:

```bash
cd system1-gateway
mvn spring-boot:run
```

Wait until you see "Started GatewayApplication" - this means it's running on **http://localhost:8081**

### Step 3: Start Frontend

Open a **third terminal** and run:

```bash
cd banking-ui
npm run dev
```

The frontend will start on **http://localhost:5173**

### Step 4: Open in Browser

Go to: **http://localhost:5173**

## Login Credentials

![Authentication Page](readme%20images/auth.jpeg)

### Customer Account
- Username: `cust1`
- Password: `pass`

### Admin Account
- Username: `admin`
- Password: `admin`

## Test Card Details

When you login as customer and want to do a top-up:
- **Card Number**: `4123456789012345`
- **PIN**: `1234`
- **Initial Balance**: $1000.00

## How to Use

1. **Login** with customer or admin credentials

2. **Customer Dashboard**:
   ![Customer Dashboard](readme%20images/customer%20dashboard.jpeg)
   - See your balance
   - View transaction history
   - Add money (top-up) using the form

   ![Customer Transaction History](readme%20images/customer%20transaction%20history.jpeg)

3. **Admin Dashboard**:
   ![Super Admin Dashboard](readme%20images/super%20admin%20dashboard.jpeg)
   - See all transactions from everyone
   - Monitor system activity

## Testing the APIs

You can test the backend directly using curl:

### Test a Top-Up
```bash
curl -X POST http://localhost:8081/api/transaction \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4123456789012345",
    "pin": "1234",
    "amount": 100,
    "type": "topup"
  }'
```

### Test a Withdrawal
```bash
curl -X POST http://localhost:8081/api/transaction \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4123456789012345",
    "pin": "1234",
    "amount": 50,
    "type": "withdraw"
  }'
```

### Check System Status
- System 1: http://localhost:8081
- System 2: http://localhost:8082

Both will show a status message instead of an error.

## Important Notes

- **Data is temporary**: The database is in-memory, so all data is lost when you restart System 2
- **Test card is auto-created**: When System 2 starts, it automatically creates a test card with $1000
- **PINs are secure**: All PINs are hashed (SHA-256) - never stored in plain text
- **Card validation**: Only cards starting with '4' are accepted

## Troubleshooting

**Port already in use?**
- Stop the service using that port
- Or change the port in `application.properties` files

**Frontend shows errors?**
- Make sure System 1 and System 2 are running first
- Check browser console (F12) for errors

**Can't connect to backend?**
- Make sure System 2 starts before System 1
- Check that ports 8081 and 8082 are not blocked

## Project Structure

```
banking-system-poc/
├── system1-gateway/       # Gateway service
├── system2-corebank/      # Core banking service
└── banking-ui/            # React frontend
```

## Features Implemented

✅ Two-tier transaction processing  
✅ Card range validation (only cards starting with '4')  
✅ Secure PIN hashing (SHA-256)  
✅ Role-based dashboards (Customer & Admin)  
✅ Transaction history  
✅ Balance management  
✅ Top-up functionality  

---

**Tech Stack**: Java 17, Spring Boot 3, React 19, TypeScript, Material-UI
