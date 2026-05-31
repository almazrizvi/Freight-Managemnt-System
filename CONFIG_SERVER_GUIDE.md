# Configuration Server Setup Guide

## Overview
The Configuration Server centralizes configuration management for all microservices. It supports multiple profiles (dev, uat, prod) for different environments.

## Directory Structure

```
config-service/
├── src/main/resources/
│   ├── application.properties          # Config Server itself
│   └── configs/                        # All service configurations
│       ├── user-service/
│       │   ├── application.properties          # Default (fallback)
│       │   ├── application-dev.properties      # Development
│       │   └── application-uat.properties      # UAT
│       └── gateway-service/
│           ├── application.properties          # Default (fallback)
│           ├── application-dev.properties      # Development
│           └── application-uat.properties      # UAT
```

## Configuration Files

### User Service Configurations

#### Default Profile (`application.properties`)
- Local PostgreSQL: `localhost:5432/freight_system`
- DDL Auto: `update`
- For fallback/production defaults

#### Dev Profile (`application-dev.properties`)
- Local PostgreSQL: `localhost:5432/freight_dev`
- DDL Auto: `create-drop` (recreate tables each startup)
- Debug logging enabled
- Show SQL queries

#### UAT Profile (`application-uat.properties`)
- External PostgreSQL: `postgres-uat.internal:5432/freight_uat`
- DDL Auto: `validate` (validate existing schema)
- Credentials from environment variables
- Production-like settings

### Gateway Service Configurations

#### Default Profile (`application.properties`)
- Routes user-service via load balancer
- CORS: Allow all origins
- For development/testing

#### Dev Profile (`application-dev.properties`)
- CORS: Allow localhost:*
- Debug logging for gateway
- More permissive settings

#### UAT Profile (`application-uat.properties`)
- CORS: Specific domain (https://freight-uat.example.com)
- Rate limiting enabled
- Production-like restrictions

## Configuration Server Endpoints

### Get Service Configuration

```bash
# Get default config
curl http://localhost:9030/user-service/default

# Get specific profile config
curl http://localhost:9030/user-service/dev
curl http://localhost:9030/user-service/uat

# Get full config with environment
curl http://localhost:9030/user-service/dev/main
```

### Response Format
```json
{
  "name": "user-service",
  "profiles": ["dev"],
  "label": "main",
  "version": "...",
  "state": null,
  "propertySources": [
    {
      "name": "classpath:/configs/user-service/application-dev.properties",
      "source": {
        "spring.application.name": "user-service",
        "server.port": "9110",
        ...
      }
    }
  ]
}
```

## How Services Use Config Server

### Service Configuration (`application.properties`)

```properties
spring.application.name=user-service
spring.config.import=optional:configserver:http://localhost:9030
spring.profiles.active=dev
```

**Explained:**
- `spring.config.import` - Tells Spring to fetch config from Config Server
- `spring.profiles.active` - Selects which profile to use (dev, uat, etc.)
- `optional:` - Won't fail if Config Server is unavailable

### Startup Sequence

1. Service starts with local `application.properties`
2. Imports from Config Server based on `spring.profiles.active`
3. Config Server returns `application-{profile}.properties`
4. Profile config overrides default config
5. Service starts with merged configuration

## Starting Services with Different Profiles

### Start Config Server (First)
```bash
cd config-service
mvn spring-boot:run
# Runs on :9030 with native file system backend
```

### Start User Service (Development)
```bash
cd user-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
# Fetches user-service dev profile from config server
# Connects to localhost:5432/freight_dev
```

### Start User Service (UAT)
```bash
cd user-service
export DB_USERNAME=postgres_uat
export DB_PASSWORD=secure_password
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=uat"
# Fetches user-service uat profile
# Connects to postgres-uat.internal using env vars
```

### Start Gateway (Development)
```bash
cd gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
# Fetches gateway-service dev profile
# CORS allows localhost
```

### Full Startup Order
```bash
# Terminal 1: Start Eureka Server
cd discovery-service && mvn spring-boot:run

# Terminal 2: Start Config Server
cd config-service && mvn spring-boot:run

# Terminal 3: Start User Service
cd user-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 4: Start Gateway
cd gateway-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 5: Start Angular Frontend
npm start
```

## Adding New Service Configuration

To add configuration for a new service (e.g., shipments-service):

### 1. Create Directory
```bash
mkdir -p config-service/src/main/resources/configs/shipments-service
```

### 2. Create Configuration Files
```bash
# Default
touch config-service/src/main/resources/configs/shipments-service/application.properties

# Dev
touch config-service/src/main/resources/configs/shipments-service/application-dev.properties

# UAT
touch config-service/src/main/resources/configs/shipments-service/application-uat.properties
```

### 3. Add Content to Each File
```properties
# application.properties (default)
spring.application.name=shipments-service
server.port=9120
eureka.client.service-url.defaultZone=http://localhost:9020/eureka/
# ... other default config

# application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/freight_dev
# ... dev-specific config

# application-uat.properties
spring.datasource.url=jdbc:postgresql://postgres-uat.internal:5432/freight_uat
# ... uat-specific config
```

### 4. Add to Gateway Routes

In `configs/gateway-service/application-dev.properties`:
```properties
spring.cloud.gateway.routes[1].id=shipments-service
spring.cloud.gateway.routes[1].uri=lb://shipments-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/shipments/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/api/(?<segment>.*),/$\\{segment}
```

### 5. Update Service application.properties

In `shipments-service/src/main/resources/application.properties`:
```properties
spring.application.name=shipments-service
spring.config.import=optional:configserver:http://localhost:9030
spring.profiles.active=dev
```

## Environment-Specific Variables

Use environment variables for sensitive data:

### In Configuration Files
```properties
# Use ${VAR_NAME:default_value} format
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# Custom values
app.api-key=${API_KEY:dev-key-12345}
app.jwt-secret=${JWT_SECRET:dev-secret}
```

### Set Environment Variables
```bash
# Linux/Mac
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password
export API_KEY=production-key
export JWT_SECRET=production-secret

# Windows
set DB_USERNAME=prod_user
set DB_PASSWORD=prod_password
```

## Fallback Configuration

If Config Server is unavailable, services have fallback config in their local `application.properties`. This ensures services can still start:

```properties
# These are fallback values
spring.datasource.url=jdbc:postgresql://localhost:5432/freight_system
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Configuration Priority (Highest to Lowest)

1. **Environment Variables** - Highest priority
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://...
   ```

2. **Command Line Arguments**
   ```bash
   mvn spring-boot:run --spring.datasource.url=jdbc:postgresql://...
   ```

3. **Config Server Profile** - Applied based on active profile
   ```
   application-{profile}.properties from config server
   ```

4. **Config Server Default**
   ```
   application.properties from config server
   ```

5. **Local application.properties** - Lowest priority (fallback)

## Monitoring & Troubleshooting

### Check Config Server Status
```bash
curl http://localhost:9030/actuator/health
```

### View Available Profiles
```bash
# List all configs known to config server
curl http://localhost:9030/user-service/profiles
```

### Debug: See Which Config is Loaded

In service logs, look for:
```
Fetching config from server at: http://localhost:9030
Located environment: name=user-service, profiles=[dev], ...
```

### Verify Service is Using Correct Config

Check service logs at startup:
```bash
# Check active profile
grep "Active profile" service.log

# Check data source URL
grep "jdbc:" service.log
```

## Common Issues

| Issue | Solution |
|-------|----------|
| Config not updating | Restart service after changing config file |
| Wrong profile used | Verify `spring.profiles.active` is set |
| Connection to wrong DB | Check environment variables & config priority |
| Config Server not found | Ensure Config Server is running on :9030 |
| UAT config not working | Verify environment variables are set |

## Best Practices

✅ **DO:**
- Use Config Server for environment-specific settings
- Store sensitive data in environment variables
- Keep default config as fallback
- Test each profile before deployment
- Document custom properties

❌ **DON'T:**
- Hardcode environment-specific values
- Check sensitive data into version control
- Change config without restarting services
- Mix profiles (use one at a time)
- Rely only on config server without fallback

## Next Steps

1. ✅ Start Config Server
2. ✅ Test accessing configurations via API
3. ✅ Start services with specific profiles
4. ✅ Verify correct database connections
5. ✅ Add configurations for remaining services
6. ✅ Implement configuration refresh without restart (Spring Cloud Bus - optional)
