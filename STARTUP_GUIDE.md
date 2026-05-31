# Quick Startup Guide

## Services Overview

| Service | Port | Purpose | Depends On |
|---------|------|---------|-----------|
| Eureka Server | 9020 | Service Discovery | None (start first) |
| Config Server | 9030 | Centralized Config | Eureka |
| User Service | 9110 | User Management API | Config Server, Eureka |
| Gateway | 9010 | API Gateway | Config Server, Eureka |
| Angular Frontend | 4201 | UI | Gateway |

## Start Services (5-Step Process)

### Step 1: Start Eureka Server
```bash
cd discovery-service
mvn spring-boot:run
```
✅ **Check:** http://localhost:9020
- Should show dashboard with no services registered yet

### Step 2: Start Config Server
```bash
# New terminal/tab
cd config-service
mvn spring-boot:run
```
✅ **Check:** http://localhost:9030/user-service/dev
- Should return JSON with merged configuration
- Config Server automatically registers with Eureka

### Step 3: Start User Service (Development Profile)
```bash
# New terminal/tab
cd user-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
✅ **Check:** 
- Logs show: "Fetching config from server at: http://localhost:9030"
- Logs show: "Located environment: name=user-service, profiles=[dev]"
- Eureka Dashboard shows user-service registered

### Step 4: Start API Gateway (Development Profile)
```bash
# New terminal/tab
cd gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
✅ **Check:**
- Gateway logs show routes configured
- Eureka Dashboard shows gateway-service registered

### Step 5: Start Angular Frontend
```bash
# New terminal/tab
cd /Users/almaz/Developer/CargoProject/Angular/freight-system
npm start
```
✅ **Check:** http://localhost:4201
- Angular app loads
- Browser console shows no CORS errors
- Can interact with UI

## Full Startup Commands (Copy-Paste Ready)

### macOS/Linux - Terminal 1: Eureka
```bash
cd ~/Developer/CargoProject/discovery-service && mvn spring-boot:run
```

### macOS/Linux - Terminal 2: Config Server
```bash
cd ~/Developer/CargoProject/config-service && mvn spring-boot:run
```

### macOS/Linux - Terminal 3: User Service
```bash
cd ~/Developer/CargoProject/user-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### macOS/Linux - Terminal 4: Gateway
```bash
cd ~/Developer/CargoProject/gateway-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### macOS/Linux - Terminal 5: Angular
```bash
cd ~/Developer/CargoProject/Angular/freight-system && npm start
```

## Alternative: Different Profiles

### Start User Service with UAT Profile
```bash
export DB_USERNAME=postgres_uat
export DB_PASSWORD=uat_password
cd user-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=uat"
```

### Start Gateway with UAT Profile
```bash
cd gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=uat"
```

## Verification Checklist

After all services are running:

- [ ] Eureka Dashboard: http://localhost:9020
  - Should show: config-service, user-service, gateway-service registered
  
- [ ] Config Server: http://localhost:9030/user-service/dev
  - Should return configuration JSON
  
- [ ] User Service Health: http://localhost:9110/actuator/health
  - Should return: `{"status":"UP"}`
  
- [ ] User Service API: http://localhost:9110/users
  - Should list users (may be empty initially)
  
- [ ] Gateway Health: http://localhost:9010/actuator/health
  - Should return: `{"status":"UP"}`
  
- [ ] Gateway Routes: http://localhost:9010/users
  - Should route to user-service through API Gateway
  
- [ ] Angular Frontend: http://localhost:4201
  - Should load without console errors
  - Login page should be visible

## Stopping Services

When done, stop services in reverse order:
1. Stop Angular (Ctrl+C in terminal)
2. Stop Gateway (Ctrl+C)
3. Stop User Service (Ctrl+C)
4. Stop Config Server (Ctrl+C)
5. Stop Eureka (Ctrl+C)

## Troubleshooting

### Issue: Port Already in Use
```bash
# Find process using port 9020 (example)
lsof -i :9020

# Kill process
kill -9 <PID>
```

### Issue: Config Server returns 404
- Make sure Config Server is running
- Check that `/configs` directory exists in config-service resources
- Verify spelling of service name and profile

### Issue: User Service can't connect to Config Server
- Check Config Server is running on :9030
- Check logs for: "Fetching config from server"
- If fails, check fallback config in service's application.properties

### Issue: Gateway can't connect to User Service
- Check User Service is running and registered in Eureka
- Check route configuration in gateway config
- Verify service names match (should be lowercase)

### Issue: Angular CORS errors
- Check gateway-service CORS configuration
- For dev, should allow all origins
- For prod, should specify allowed origins

## Logs to Watch

### Config Server Successfully Started
```
Started ConfigServiceApplication
Spring Cloud Config Server
```

### User Service Fetched Config
```
Fetching config from server at: http://localhost:9030
Located environment: name=user-service, profiles=[dev]
```

### Gateway Routes Configured
```
Successfully added route
Mapped gateway routes
```

### Services Registered with Eureka
```
Registering application USER-SERVICE with eureka with initial status UP
```

## What Each Service Does

### 1. Eureka Server (Port 9020)
- Central service registry
- All services register with Eureka
- All services discover others via Eureka
- Dashboard at http://localhost:9020

### 2. Config Server (Port 9030)
- Serves configuration files to services
- Sources: `/configs/user-service/application-{profile}.properties`
- Services request config at startup
- Returns merged configuration

### 3. User Service (Port 9110)
- REST API for user CRUD operations
- Endpoints: GET/POST/PUT/DELETE /users
- Fetches config from Config Server
- Connects to PostgreSQL database
- Registers with Eureka

### 4. API Gateway (Port 9010)
- Entry point for all API requests
- Routes requests to appropriate microservices
- Handles CORS
- Example: `/api/users` → `lb://user-service/users`
- Discovers services via Eureka

### 5. Angular Frontend (Port 4201)
- User interface in browser
- Calls Gateway API at `/api/users`
- Gateway routes to User Service
- Displays user list, forms, and admin panels

## Configuration Flow

```
User Browser
    ↓
Angular App (localhost:4201)
    ↓
API Gateway (localhost:9010)
    ↓ [routes to user-service via Eureka]
User Service (localhost:9110)
    ↓
PostgreSQL Database
    ↑
Config from Config Server (localhost:9030)
    ↑
Config Files (/configs/user-service/application-dev.properties)
```

## Next Steps

1. ✅ Start all services following steps above
2. ✅ Verify checklist items passing
3. ✅ Add more microservices (shipments, vehicles, customers)
4. ✅ Add each service to config server with dev/uat profiles
5. ✅ Add routes to gateway for new services
6. ✅ Implement authentication and authorization
