# Pre-Startup Checklist & Quick Reference

## ✅ Pre-Startup Checklist

### System Requirements
- [ ] Java 21+ installed: `java -version`
- [ ] Maven 3.9+ installed: `mvn -version`
- [ ] Node.js 20+ installed: `node -v`
- [ ] npm 10+ installed: `npm -v`
- [ ] PostgreSQL 15+ installed: `postgres --version`
- [ ] PostgreSQL service running
- [ ] 6 terminal windows/tabs available

### Database Verification
```bash
# Connect to PostgreSQL
psql -U postgres

# In psql prompt:
CREATE DATABASE freight_system;
CREATE DATABASE freight_dev;
\q
```
- [ ] freight_system database exists
- [ ] freight_dev database exists

### Project Structure
- [ ] discovery-service exists at ~/Developer/CargoProject/
- [ ] config-service exists with /src/main/resources/configs/ directory
- [ ] user-service exists
- [ ] gateway-service exists
- [ ] Angular project at ~/Developer/CargoProject/Angular/freight-system

### File Verification
```bash
# Verify config directories exist
ls -la ~/Developer/CargoProject/config-service/src/main/resources/configs/
```
- [ ] configs/ directory exists
- [ ] user-service/ subdirectory exists
- [ ] gateway-service/ subdirectory exists

### Dependencies
```bash
cd ~/Developer/CargoProject/user-service
mvn clean compile
```
- [ ] Compile succeeds with no errors
- [ ] Check internet connection for Maven downloads

---

## 🚀 Startup Sequence (Copy-Paste Commands)

### Terminal 1: Eureka Server (Start FIRST)
```bash
cd ~/Developer/CargoProject/discovery-service
mvn clean spring-boot:run
```
**Expected Output:**
```
Started DiscoveryServiceApplication
Eureka Server Started
```
**Verification:** http://localhost:9020 shows dashboard

### Terminal 2: Config Server
```bash
cd ~/Developer/CargoProject/config-service
mvn clean spring-boot:run
```
**Expected Output:**
```
Started ConfigServiceApplication
Locating property source
Located native property source
```
**Verification:** `curl http://localhost:9030/user-service/dev` returns JSON

---

**⏸️ WAIT:** Before starting services 3-4, verify steps 1-2 above are working!

---

### Terminal 3: User Service (Dev Profile)
```bash
cd ~/Developer/CargoProject/user-service
export SPRING_PROFILES_ACTIVE=dev
mvn clean spring-boot:run
```
**OR use command line argument (if export doesn't work):**
```bash
cd ~/Developer/CargoProject/user-service
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Expected Output:**
```
Fetching config from server at: http://localhost:9030
Located environment: name=user-service, profiles=[dev]
Application 'user-service' is STARTING
Started user-service in X.XXX seconds
```

**Verification:**
- `curl http://localhost:9110/actuator/health` returns `{"status":"UP"}`
- Eureka dashboard shows user-service registered

### Terminal 4: Gateway Service (Dev Profile)
```bash
cd ~/Developer/CargoProject/gateway-service
export SPRING_PROFILES_ACTIVE=dev
mvn clean spring-boot:run
```
**Expected Output:**
```
Fetching config from server at: http://localhost:9030
Located environment: name=gateway-service, profiles=[dev]
Application 'gateway-service' is STARTING
Successfully added route with id: user-service
Started gateway-service in X.XXX seconds
```

**Verification:**
- `curl http://localhost:9010/actuator/health` returns `{"status":"UP"}`
- Eureka dashboard shows gateway-service registered

### Terminal 5: Angular Frontend
```bash
cd ~/Developer/CargoProject/Angular/freight-system
npm start
```
**Expected Output:**
```
✔ Compiled successfully
Local: http://localhost:4201
```

**Verification:**
- Browser opens to http://localhost:4201
- Angular app loads without errors
- Browser console has no red errors

---

## ✅ Post-Startup Verification Checklist

### Eureka Server (http://localhost:9020)
- [ ] Dashboard accessible
- [ ] Shows "Application Instance" list
- [ ] 3 instances registered:
  - [ ] CONFIG-SERVICE (URL: http://xxxx:9030/...)
  - [ ] USER-SERVICE (URL: http://xxxx:9110/...)
  - [ ] GATEWAY-SERVICE (URL: http://xxxx:9010/...)
- [ ] All show status "UP" (green)
- [ ] Lease Renewal: green status

### Config Server Endpoints
```bash
curl http://localhost:9030/user-service/default
curl http://localhost:9030/user-service/dev
curl http://localhost:9030/gateway-service/dev
```
- [ ] All requests return 200 OK with JSON
- [ ] Response includes environmental properties
- [ ] Database URL shows for user-service (localhost:5432 for dev)

### User Service API
```bash
curl http://localhost:9110/users
```
- [ ] Returns 200 OK
- [ ] Returns empty array `[]` or existing users
- [ ] No error messages in response

### Gateway Routing
```bash
curl http://localhost:9010/users
```
- [ ] Returns same as /users endpoint
- [ ] Indicates successful routing to user-service
- [ ] No 503 or routing errors

### Angular UI
```
http://localhost:4201
```
- [ ] Page loads in browser
- [ ] Login page visible
- [ ] No 404 errors for assets
- [ ] No CORS errors in browser console
- [ ] No red errors in Console tab

---

## 🔧 Common Commands Reference

### View Active Status

```bash
# Eureka Dashboard (visual)
open http://localhost:9020

# All services health (JSON)
curl http://localhost:9020/eureka/apps

# User Service health
curl http://localhost:9110/actuator/health

# Gateway health
curl http://localhost:9010/actuator/health

# Config Server health
curl http://localhost:9030/actuator/health
```

### Database Commands

```bash
# Connect to PostgreSQL
psql -U postgres -d freight_system

# List all tables
\dt

# Check users table
SELECT * FROM users;

# Check user count
SELECT COUNT(*) FROM users;

# View table schema
\d users

# Exit
\q
```

### Test User CRUD

```bash
# Create user
curl -X POST http://localhost:9010/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Test User",
    "userType": "CUSTOMER",
    "password": "password123"
  }'

# Get all users
curl http://localhost:9010/users

# Get specific user (replace UUID)
curl http://localhost:9010/users/{user-id}

# Delete user (soft delete)
curl -X DELETE http://localhost:9010/users/{user-id}?deletedBy={user-id}

# Search users
curl "http://localhost:9010/users/search?query=test"

# Filter by type
curl http://localhost:9010/users/type/CUSTOMER
```

### Start Services Individually (Without Maven)

If services are already running in background:

```bash
# Check if running
lsof -i :9020   # Eureka
lsof -i :9030   # Config Server
lsof -i :9110   # User Service
lsof -i :9010   # Gateway
lsof -i :4201   # Angular

# Kill specific port (example: port 9020)
lsof -i :9020 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

### View Logs

```bash
# User Service logs (while running)
# Check terminal where user-service is running

# Search logs for errors
# [Ctrl+C] to stop, then:
cd ~/Developer/CargoProject/user-service
mvn clean spring-boot:run 2>&1 | grep -i error

# View last 50 lines of log
tail -50 ~/.m2/... (if logging to file)
```

### Change Configuration Profile

```bash
# Start with UAT profile instead of dev
cd ~/Developer/CargoProject/user-service
export DB_USERNAME=postgres_uat
export DB_PASSWORD=uat_password
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=uat"

# Start with default profile
cd ~/Developer/CargoProject/user-service
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=default"
```

### Build & Validation

```bash
# Build all backend services
cd ~/Developer/CargoProject/user-service && mvn clean --quiet && echo "✓ user-service"
cd ~/Developer/CargoProject/gateway-service && mvn clean --quiet && echo "✓ gateway-service"
cd ~/Developer/CargoProject/config-service && mvn clean --quiet && echo "✓ config-service"
cd ~/Developer/CargoProject/discovery-service && mvn clean --quiet && echo "✓ discovery-service"

# Build Angular
cd ~/Developer/CargoProject/Angular/freight-system && npm run build --quiet && echo "✓ angular-frontend"
```

---

## 🆘 Quick Troubleshooting

### Issue: "Connection refused" on port 9030
```bash
# Is Config Server running?
lsof -i :9030

# If empty, start it:
cd ~/Developer/CargoProject/config-service
mvn clean spring-boot:run
```

### Issue: "Service not found" in gateway logs
```bash
# Check Eureka for registered services
curl http://localhost:9020/eureka/apps

# If user-service missing, verify it's running:
lsof -i :9110
```

### Issue: Angular CORS error
```bash
# Check gateway CORS config is loaded
curl http://localhost:9030/gateway-service/dev | grep -i cors

# Restart gateway with dev profile
# See "Startup Sequence" section above
```

### Issue: Database connection failed
```bash
# Check PostgreSQL is running
psql -U postgres -c "SELECT 1"

# Check freight_system database exists
psql -U postgres -d freight_system -c "\dt"

# If missing, create:
psql -U postgres -c "CREATE DATABASE freight_system"
psql -U postgres -c "CREATE DATABASE freight_dev"
```

### Issue: Maven download hangs
```bash
# It's likely downloading dependencies (~500MB first time)
# Give it 5-10 minutes the first run

# Or, update Maven cache:
cd ~/Developer/CargoProject/user-service
rm -rf ~/.m2/repository  # WARNING: affects all projects
mvn clean compile        # Re-download everything
```

---

## 📱 Test Workflow (After Startup Verified)

### 1. Create a User via Angular UI
- Navigate to http://localhost:4201
- Click "Create User" (or appropriate menu item)
- Fill form: email, name, password, type
- Click Save
- Should see success message

### 2. Verify in Database
```bash
psql -U postgres -d freight_system
SELECT * FROM users ORDER BY created_at DESC LIMIT 1;
\q
```
- User should appear in database
- Password stored as hash (not plain text)
- created_at timestamp should be recent

### 3. View User in List
- Return to UI
- Navigate to Users List
- New user should appear
- Can search by email/name
- Can filter by type

### 4. Update User
- Click Edit on user row
- Change name/email
- Click Save
- User should update in list

### 5. Delete User
- Click Delete on user row
- Confirm deletion
- User should disappear from list

### 6. Verify Soft Delete in Database
```bash
psql -U postgres -d freight_system
SELECT id, email, deleted_at, deleted_by FROM users WHERE email = 'your-email';
\q
```
- deleted_at should have timestamp
- deleted_by should have UUID
- But data is NOT deleted (can be restored)

---

## 🎯 Success Indicators

**All of these should be true after successful startup:**

- [ ] 5 terminals have running services (no error messages)
- [ ] Eureka dashboard shows 3 services UP
- [ ] Config Server endpoints return valid JSON
- [ ] Angular UI loads in browser without errors
- [ ] Can create a user in Angular UI
- [ ] New user appears in database immediately
- [ ] Can view user in Angular list
- [ ] Can search/filter users in Angular
- [ ] Can edit user and changes persist
- [ ] Can delete user and it's soft-deleted in database
- [ ] Browser console has no red errors
- [ ] No services show timeout errors in logs

**If all of the above are true: ✅ System is ready!**

---

## 🔄 Service Dependency Map

```
Startup Order:
1. Eureka Server ← Foundation (everything needs this)
2. Config Server ← Provides config to services
3. User Service ← Provides API
4. Gateway ← Routes requests
5. Angular ← Frontend UI

This order matters! Each depends on previous ones.
```

```
Runtime Calls:
Chrome Browser
    ↓
http://localhost:4201 (Angular)
    ↓
http://localhost:9010/api/* (Gateway)
    ↓ [via Eureka load balancer]
http://localhost:9110/* (User Service)
    ↓
PostgreSQL Database
```

---

**Print or save this page for quick reference during development!**
