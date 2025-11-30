# Deployment Guide for Render

This guide will help you deploy the entire Banking System POC to Render's free tier.

## Prerequisites

1. A GitHub account
2. A Render account (sign up at https://render.com - free tier available)
3. Your code pushed to GitHub

## Step-by-Step Deployment

### Step 1: Push Code to GitHub

Make sure all your code is pushed to your GitHub repository:
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### Step 2: Deploy to Render

#### Option A: Using render.yaml (Recommended)

1. Go to https://dashboard.render.com
2. Click "New" → "Blueprint"
3. Connect your GitHub repository
4. Render will automatically detect `render.yaml` and create all 3 services
5. **Important**: After services are created, you need to manually set environment variables for the frontend:
   - Go to `banking-ui` service → Environment
   - Add:
     - `VITE_API_BASE_URL` = `https://system1-gateway.onrender.com`
     - `VITE_SYSTEM2_BASE_URL` = `https://system2-corebank.onrender.com`
6. Redeploy the frontend service after setting environment variables

#### Option B: Manual Deployment (3 Services)

**Deploy System 2 (Core Banking) First:**

1. Click "New" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - **Name**: `system2-corebank`
   - **Environment**: `Java`
   - **Region**: `Oregon` (or closest to you)
   - **Branch**: `main` (or your branch)
   - **Root Directory**: `system2-corebank`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Plan**: `Free`
4. Add Environment Variable:
   - `JAVA_VERSION` = `17`
5. Click "Create Web Service"
6. Wait for deployment and note the URL (e.g., `https://system2-corebank.onrender.com`)

**Deploy System 1 (Gateway):**

1. Click "New" → "Web Service"
2. Connect the same GitHub repository
3. Configure:
   - **Name**: `system1-gateway`
   - **Environment**: `Java`
   - **Region**: `Oregon` (same as System 2)
   - **Branch**: `main`
   - **Root Directory**: `system1-gateway`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Plan**: `Free`
4. Add Environment Variables:
   - `JAVA_VERSION` = `17`
   - `SYSTEM2_URL` = `https://system2-corebank.onrender.com` (use the URL from System 2)
5. Click "Create Web Service"
6. Wait for deployment and note the URL (e.g., `https://system1-gateway.onrender.com`)

**Deploy Frontend (React App):**

1. Click "New" → "Static Site" (or "Web Service" if Static Site not available)
2. Connect the same GitHub repository
3. Configure:
   - **Name**: `banking-ui`
   - **Environment**: `Node`
   - **Region**: `Oregon` (same as others)
   - **Branch**: `main`
   - **Root Directory**: `banking-ui`
   - **Build Command**: `npm ci && npm run build`
   - **Publish Directory**: `dist`
   - **Plan**: `Free`
4. Add Environment Variables:
   - `NODE_VERSION` = `18`
   - `VITE_API_BASE_URL` = `https://system1-gateway.onrender.com`
   - `VITE_SYSTEM2_BASE_URL` = `https://system2-corebank.onrender.com`
5. Click "Create Static Site" (or "Create Web Service")
6. Wait for deployment

### Step 3: Update CORS Settings (If Needed)

If you encounter CORS errors, you may need to update the CORS settings in the backend services:

1. Go to `system1-gateway` service → Environment
2. Add: `CORS_ORIGINS` = `https://banking-ui.onrender.com` (your frontend URL)
3. Redeploy the service

### Step 4: Test Your Deployment

1. Visit your frontend URL (e.g., `https://banking-ui.onrender.com`)
2. Try logging in with:
   - Customer: `cust1` / `pass`
   - Admin: `admin` / `admin`
3. Test a transaction with card: `4123456789012345`, PIN: `1234`

## Important Notes

### Free Tier Limitations

- **Sleep Mode**: Free tier services spin down after 15 minutes of inactivity
- **Cold Starts**: First request after sleep can take 30-60 seconds
- **Build Time**: Free tier has limited build minutes per month
- **Bandwidth**: 100 GB/month included

### Service URLs

After deployment, your services will have URLs like:
- Frontend: `https://banking-ui.onrender.com`
- System 1: `https://system1-gateway.onrender.com`
- System 2: `https://system2-corebank.onrender.com`

### Troubleshooting

**Services not starting?**
- Check build logs in Render dashboard
- Ensure Java 17 is specified
- Verify Maven build completes successfully

**CORS errors?**
- Make sure frontend URL is added to `CORS_ORIGINS` in System 1
- Check that environment variables are set correctly

**Frontend can't connect to backend?**
- Verify `VITE_API_BASE_URL` and `VITE_SYSTEM2_BASE_URL` are set correctly
- Make sure backend services are running (not sleeping)
- Check browser console for errors

**Database resets?**
- This is expected! The H2 database is in-memory, so data is lost on restart
- For persistent data, you'd need to upgrade to a paid plan and use PostgreSQL

## Next Steps

- Monitor your services in the Render dashboard
- Set up auto-deploy from GitHub (enabled by default)
- Consider upgrading to paid plans for production use
- Add custom domains if needed

## Support

If you encounter issues:
1. Check Render service logs
2. Verify all environment variables are set
3. Ensure services are in the same region
4. Check Render status page: https://status.render.com

