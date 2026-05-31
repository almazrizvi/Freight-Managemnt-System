# Adding New Microservices - Implementation Guide

## Phase 2: Expanding the Microservices Ecosystem

### Current Architecture
- **User Service** (9110) - User management, CRUD
- **Gateway** (9010) - Request routing
- **Config Server** (9030) - Centralized configuration
- **Eureka** (9020) - Service discovery

### Planned Microservices
1. **Shipments Service** (9120)
2. **Vehicles Service** (9130)
3. **Customers Service** (9140)
4. **Reports Service** (9150)

---

## 🏗️ Architecture Pattern: Service Template

All new services follow the same pattern as User Service:

```
service-name/
├── pom.xml
├── src/main/resources/
│   └── application.properties (bootstrap config)
├── src/main/java/.../
│   ├── ServiceNameApplication.java (@EnableDiscoveryClient)
│   ├── controller/
│   │   └── ServiceNameController.java
│   ├── service/
│   │   └── ServiceNameService.java
│   ├── model/
│   │   └── Entity.java
│   ├── dto/
│   │   └── Entity.java
│   └── repository/
│       └── EntityRepository.java
└── target/
```

---

## 📋 Checklist: Adding a New Service

### 1. Create Maven Project Structure

```bash
cd ~/Developer/CargoProject

# Create shipments-service
mkdir -p shipments-service/src/main/{java,resources}

# Create Java package structure
mkdir -p shipments-service/src/main/java/com/freight/shipments/{model,service,controller,repository,dto}
```

### 2. Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.11</version>
    </parent>
    
    <groupId>com.freight</groupId>
    <artifactId>shipments-service</artifactId>
    <version>1.0.0</version>
    <name>Shipments Service</name>
    
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2024.0.1</spring-cloud.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        
        <!-- Spring Cloud Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.2</version>
        </dependency>
        
        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. Create Application Class

**File:** `shipments-service/src/main/java/com/freight/shipments/ShipmentsServiceApplication.java`

```java
package com.freight.shipments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShipmentsServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ShipmentsServiceApplication.class, args);
    }
}
```

### 4. Create bootstrap application.properties

**File:** `shipments-service/src/main/resources/application.properties`

```properties
spring.application.name=shipments-service
server.port=9120

# Config Server
spring.config.import=optional:configserver:http://localhost:9030
spring.profiles.active=dev

# Eureka
eureka.client.service-url.defaultZone=http://localhost:9020/eureka/

# Fallback config (if config server unavailable)
spring.datasource.url=jdbc:postgresql://localhost:5432/freight_system
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

logging.level.root=INFO
```

### 5. Add to Config Server

**Directory:** Create in config-service

```bash
mkdir -p config-service/src/main/resources/configs/shipments-service
```

**File:** `config-service/src/main/resources/configs/shipments-service/application.properties`

```properties
spring.application.name=shipments-service
server.port=9120
eureka.client.service-url.defaultZone=http://localhost:9020/eureka/

spring.datasource.url=jdbc:postgresql://localhost:5432/freight_system
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

logging.level.root=INFO
```

**File:** `config-service/src/main/resources/configs/shipments-service/application-dev.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/freight_dev
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

logging.level.root=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**File:** `config-service/src/main/resources/configs/shipments-service/application-uat.properties`

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:postgres-uat.internal}:${DB_PORT:5432}/freight_uat
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

spring.jpa.hibernate.ddl-auto=validate

logging.level.root=WARN
```

### 6. Add Gateway Route

**File:** `config-service/src/main/resources/configs/gateway-service/application.properties`

Add to existing routes:

```properties
# ... existing routes ...

spring.cloud.gateway.routes[1].id=shipments-service
spring.cloud.gateway.routes[1].uri=lb://shipments-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/shipments/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/api/(?<segment>.*),/$\{segment}
```

**File:** `config-service/src/main/resources/configs/gateway-service/application-dev.properties`

```properties
# ... existing config ...

# Add shipments route
spring.cloud.gateway.routes[1].id=shipments-service
spring.cloud.gateway.routes[1].uri=lb://shipments-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/shipments/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/api/(?<segment>.*),/$\{segment}
```

---

## 🗄️ Data Model Example: Shipment Service

### Database Schema

```sql
CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID NOT NULL REFERENCES users(id),
    vehicle_id UUID REFERENCES vehicles(id),
    origin_location VARCHAR(255) NOT NULL,
    destination_location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    weight_kg DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    deleted_by UUID REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS shipment_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_customer ON shipments(customer_id);
CREATE INDEX idx_shipments_deleted ON shipments(deleted_at);
```

### Entity Class

```java
package com.freight.shipments.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
public class Shipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String trackingNumber;
    
    @Column(nullable = false)
    private UUID customerId;
    
    private UUID vehicleId;
    
    @Column(nullable = false)
    private String originLocation;
    
    @Column(nullable = false)
    private String destinationLocation;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ShipmentStatus status = ShipmentStatus.PENDING;
    
    private Double weightKg;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
    private UUID deletedBy;
    
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentItem> items = new ArrayList<>();
}

enum ShipmentStatus {
    PENDING, IN_TRANSIT, DELIVERED, CANCELLED
}
```

### Repository

```java
package com.freight.shipments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.freight.shipments.model.Shipment;
import java.util.*;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    
    List<Shipment> findByDeletedAtIsNullOrderByCreatedAtDesc();
    
    List<Shipment> findByDeletedAtIsNullAndCustomerIdOrderByCreatedAtDesc(UUID customerId);
    
    List<Shipment> findByDeletedAtIsNullAndStatusOrderByCreatedAtDesc(String status);
    
    Optional<Shipment> findByIdAndDeletedAtIsNull(UUID id);
    
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
```

### Service Layer

```java
package com.freight.shipments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.freight.shipments.model.Shipment;
import com.freight.shipments.repository.ShipmentRepository;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class ShipmentService {
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findByDeletedAtIsNullOrderByCreatedAtDesc();
    }
    
    public Optional<Shipment> getShipmentById(UUID id) {
        return shipmentRepository.findByIdAndDeletedAtIsNull(id);
    }
    
    public Shipment createShipment(Shipment shipment) {
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }
    
    public Shipment updateShipment(UUID id, Shipment shipmentDetails) {
        Shipment shipment = getShipmentById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        shipment.setOriginLocation(shipmentDetails.getOriginLocation());
        shipment.setDestinationLocation(shipmentDetails.getDestinationLocation());
        shipment.setStatus(shipmentDetails.getStatus());
        shipment.setUpdatedAt(LocalDateTime.now());
        
        return shipmentRepository.save(shipment);
    }
    
    public void deleteShipment(UUID id, UUID deletedBy) {
        Shipment shipment = getShipmentById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        shipment.setDeletedAt(LocalDateTime.now());
        shipment.setDeletedBy(deletedBy);
        shipmentRepository.save(shipment);
    }
    
    public List<Shipment> getShipmentsByCustomer(UUID customerId) {
        return shipmentRepository.findByDeletedAtIsNullAndCustomerIdOrderByCreatedAtDesc(customerId);
    }
    
    public List<Shipment> getShipmentsByStatus(String status) {
        return shipmentRepository.findByDeletedAtIsNullAndStatusOrderByCreatedAtDesc(status);
    }
}
```

### Controller

```java
package com.freight.shipments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.freight.shipments.model.Shipment;
import com.freight.shipments.service.ShipmentService;
import java.util.*;

@RestController
@RequestMapping("/shipments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShipmentController {
    
    @Autowired
    private ShipmentService shipmentService;
    
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable UUID id) {
        return shipmentService.getShipmentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.ok(shipmentService.createShipment(shipment));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(
            @PathVariable UUID id,
            @RequestBody Shipment shipmentDetails) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, shipmentDetails));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable UUID id,
            @RequestParam UUID deletedBy) {
        shipmentService.deleteShipment(id, deletedBy);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Shipment> getShipmentsByCustomer(@PathVariable UUID customerId) {
        return shipmentService.getShipmentsByCustomer(customerId);
    }
    
    @GetMapping("/status/{status}")
    public List<Shipment> getShipmentsByStatus(@PathVariable String status) {
        return shipmentService.getShipmentsByStatus(status);
    }
}
```

---

## 🔗 Frontend: Adding Service to Angular

### 1. Create Shipment Service

**File:** `src/app/core/shipment.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Shipment {
  id: string;
  trackingNumber: string;
  customerId: string;
  vehicleId?: string;
  originLocation: string;
  destinationLocation: string;
  status: 'PENDING' | 'IN_TRANSIT' | 'DELIVERED' | 'CANCELLED';
  weightKg?: number;
  createdAt: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ShipmentService {
  private apiUrl = 'http://localhost:9010/api/shipments';

  constructor(private http: HttpClient) { }

  getAllShipments(): Observable<Shipment[]> {
    return this.http.get<Shipment[]>(this.apiUrl);
  }

  getShipmentById(id: string): Observable<Shipment> {
    return this.http.get<Shipment>(`${this.apiUrl}/${id}`);
  }

  createShipment(shipment: Shipment): Observable<Shipment> {
    return this.http.post<Shipment>(this.apiUrl, shipment);
  }

  updateShipment(id: string, shipment: Shipment): Observable<Shipment> {
    return this.http.put<Shipment>(`${this.apiUrl}/${id}`, shipment);
  }

  deleteShipment(id: string, deletedBy: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}?deletedBy=${deletedBy}`);
  }

  getShipmentsByCustomer(customerId: string): Observable<Shipment[]> {
    return this.http.get<Shipment[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  getShipmentsByStatus(status: string): Observable<Shipment[]> {
    return this.http.get<Shipment[]>(`${this.apiUrl}/status/${status}`);
  }
}
```

### 2. Create Components

```bash
ng generate component features/shipments-list
ng generate component features/shipment-form
ng generate component features/shipment-detail
```

### 3. Add Routing

**File:** `src/app/layout/layout-routing.module.ts`

```typescript
const routes = [
  {
    path: 'admin/shipments',
    component: ShipmentsListComponent
  },
  {
    path: 'admin/shipments/create',
    component: ShipmentFormComponent
  },
  {
    path: 'admin/shipments/:id/edit',
    component: ShipmentFormComponent
  },
  {
    path: 'admin/shipments/:id',
    component: ShipmentDetailComponent
  }
  // ... other routes
];
```

---

## 🧪 Testing New Service

### 1. Start Services

```bash
# Terminal 1: Config Server (includes new shipments config)
cd config-service && mvn clean spring-boot:run

# Terminal 2: Shipments Service
cd shipments-service && mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 3: Gateway
cd gateway-service && mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 2. Test API Endpoints

```bash
# Get all shipments
curl http://localhost:9010/shipments

# Create shipment
curl -X POST http://localhost:9010/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SHP-2024-001",
    "customerId": "...",
    "originLocation": "New York",
    "destinationLocation": "Los Angeles",
    "status": "PENDING",
    "weightKg": 150.5
  }'

# Get shipments by customer
curl http://localhost:9010/shipments/customer/{customerId}

# Get shipments by status
curl http://localhost:9010/shipments/status/IN_TRANSIT
```

### 3. Verify in Eureka

- Visit http://localhost:9020
- Should see SHIPMENTS-SERVICE registered

### 4. Test via Angular

- Create UI component
- Call ShipmentService.getAllShipments()
- Verify data displays, CRUD works

---

## 📋 Repeat for Other Services

Follow the same pattern for:
- **Vehicles Service** (9130)
- **Customers Service** (9140)
- **Reports Service** (9150)

Each service requires:
1. Maven project structure
2. Entity, Repository, Service, Controller classes
3. Database schema and tables
4. Bootstrap application.properties
5. Config Server profiles (default, dev, uat)
6. Gateway route configuration
7. Angular service and components

---

## 🔄 Service Dependencies

```
Angular UI
    ↓
Gateway (9010)
    ├─→ User Service (9110)
    ├─→ Shipments Service (9120)
    ├─→ Vehicles Service (9130)
    ├─→ Customers Service (9140)
    └─→ Reports Service (9150)
         ↓
    Config Server (9030) - provides config to each
         ↓
    Eureka (9020) - knows all services
         ↓
    PostgreSQL - all services connect
```

---

## ✅ Checklist: Add New Service

- [ ] Create Maven project directory
- [ ] Create pom.xml with Spring Boot, Cloud dependencies
- [ ] Create Application class with @EnableDiscoveryClient
- [ ] Create bootstrap application.properties
- [ ] Create Entity class with UUID ID, soft delete fields
- [ ] Create Repository with custom queries
- [ ] Create Service class with CRUD and business logic
- [ ] Create Controller with REST endpoints
- [ ] Create database schema SQL script
- [ ] Add to Config Server: /configs/service-name/
- [ ] Add application.properties (default)
- [ ] Add application-dev.properties (dev)
- [ ] Add application-uat.properties (uat)
- [ ] Add route to gateway configuration
- [ ] Update gateway-service/application-dev.properties
- [ ] Create Angular service (HttpClient wrapper)
- [ ] Create Angular components (list, form, detail)
- [ ] Add routes to routing module
- [ ] Test API via curl
- [ ] Test via Angular UI
- [ ] Verify in Eureka dashboard

---

## 🚀 Deployment Considerations

Each new service should have:
- [ ] Health endpoint: `/actuator/health`
- [ ] Metrics endpoint: `/actuator/metrics`
- [ ] Graceful startup/shutdown
- [ ] Proper logging configuration
- [ ] Database schema creation/validation
- [ ] Error handling and exception mapping
- [ ] CORS properly configured
- [ ] Security: Input validation, sanitization
- [ ] Documentation: API endpoints, data models
- [ ] Test coverage: Unit, integration tests

