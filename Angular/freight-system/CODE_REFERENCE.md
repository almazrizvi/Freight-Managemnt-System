# Angular Layout Refactoring - Code Reference

## 1. MenuService

**Location**: `src/app/shared/services/menu.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface MenuItem {
  label: string;
  icon: string;
  route: string;
  children?: MenuItem[];
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private defaultMenuItems: MenuItem[] = [
    { label: 'Shipments', icon: 'local_shipping', route: '/shipments' },
    { label: 'Vehicles', icon: 'directions_car', route: '/vehicles' },
    { label: 'Customers', icon: 'people', route: '/customers' },
    { label: 'Reports', icon: 'assessment', route: '/reports' }
  ];

  constructor() {}

  // Returns Observable of menu items
  // Later: replace with this.http.get<MenuItem[]>('/api/menu-items')
  getMenuItems(): Observable<MenuItem[]> {
    return of(this.defaultMenuItems);
  }
}
```

---

## 2. MainLayoutComponent

**Location**: `src/app/layout/main-layout/main-layout.component.ts`

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

import { Header } from '../header/header';
import { Sidebar } from '../sidebar/sidebar';
import { Footer } from '../footer/footer';

@Component({
  standalone: true,
  selector: 'app-main-layout',
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    Header,
    Sidebar,
    Footer
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  showHeader = true;
  showSidebar = true;
  showFooter = true;
  sidenavOpened = true;

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.updateLayoutVisibility();
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.updateLayoutVisibility();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private updateLayoutVisibility(): void {
    const data = this.activatedRoute.snapshot.firstChild?.data || {};
    this.showHeader = data['showHeader'] !== false;
    this.showSidebar = data['showSidebar'] !== false;
    this.showFooter = data['showFooter'] !== false;
  }

  toggleSidebar(): void {
    this.sidenavOpened = !this.sidenavOpened;
  }
}
```

---

## 3. Layout Template

**Location**: `src/app/layout/main-layout/main-layout.component.html`

```html
<mat-toolbar
  color="primary"
  class="fixed-header"
  *ngIf="showHeader"
>
  <button
    mat-icon-button
    (click)="toggleSidebar()"
    *ngIf="showSidebar"
    aria-label="Toggle sidebar"
  >
    <mat-icon>menu</mat-icon>
  </button>
  <span class="logo">🚚 Freight Management System</span>
  <span class="spacer"></span>
  <button mat-icon-button aria-label="Profile">
    <mat-icon>account_circle</mat-icon>
  </button>
</mat-toolbar>

<mat-sidenav-container
  class="sidenav-container"
  [ngClass]="{ 'with-header': showHeader }"
>
  <mat-sidenav
    #sidenav
    class="sidenav"
    mode="side"
    [opened]="sidenavOpened"
    *ngIf="showSidebar"
  >
    <app-sidebar></app-sidebar>
  </mat-sidenav>

  <mat-sidenav-content class="main-content">
    <main>
      <router-outlet></router-outlet>
    </main>
  </mat-sidenav-content>
</mat-sidenav-container>

<app-footer *ngIf="showFooter"></app-footer>
```

---

## 4. Layout Styles

**Location**: `src/app/layout/main-layout/main-layout.component.scss`

```scss
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-weight: 600;
  font-size: 18px;
  margin-left: 16px;
}

.spacer {
  flex: 1 1 auto;
}

.sidenav-container {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;

  &.with-header {
    top: 64px;
  }
}

.sidenav {
  width: 250px;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;

  main {
    flex: 1;
    padding: 24px;
    background-color: #f5f5f5;
  }
}

// Mobile responsiveness
@media (max-width: 768px) {
  .sidenav {
    width: 200px;
  }
}

@media (max-width: 600px) {
  .logo {
    font-size: 16px;
    margin-left: 8px;
  }

  .sidenav {
    width: 100%;
    max-width: 250px;
  }

  .main-content main {
    padding: 12px;
  }
}
```

---

## 5. Sidebar Component

**Location**: `src/app/layout/sidebar/sidebar.ts`

```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MenuService, MenuItem } from '../../shared/services/menu.service';

@Component({
  standalone: true,
  selector: 'app-sidebar',
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class Sidebar implements OnInit {
  menuItems: MenuItem[] = [];

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.menuService.getMenuItems().subscribe(items => {
      this.menuItems = items;
    });
  }
}
```

**Location**: `src/app/layout/sidebar/sidebar.html`

```html
<nav class="sidebar-nav">
  <h2 class="nav-title">Menu</h2>
  <mat-nav-list>
    <mat-list-item
      *ngFor="let item of menuItems"
      [routerLink]="item.route"
      routerLinkActive="active"
      [routerLinkActiveOptions]="{ exact: false }"
      class="menu-item"
    >
      <mat-icon matListItemIcon>{{ item.icon }}</mat-icon>
      <span matListItemTitle>{{ item.label }}</span>
    </mat-list-item>
  </mat-nav-list>
</nav>
```

---

## 6. Layout Routing Module

**Location**: `src/app/layout/layout-routing.module.ts`

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './main-layout/main-layout.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    },
    children: [
      {
        path: 'shipments',
        data: { showHeader: true, showSidebar: true, showFooter: true },
        loadChildren: () => import('../features/shipments/shipments.module')
          .then(m => m.ShipmentsModule)
      },
      {
        path: 'vehicles',
        data: { showHeader: true, showSidebar: true, showFooter: true },
        loadChildren: () => import('../features/vehicles/vehicles.module')
          .then(m => m.VehiclesModule)
      },
      {
        path: 'customers',
        data: { showHeader: true, showSidebar: true, showFooter: true },
        loadChildren: () => import('../features/customers/customers.module')
          .then(m => m.CustomersModule)
      },
      {
        path: 'reports',
        data: { showHeader: true, showSidebar: true, showFooter: true },
        loadChildren: () => import('../features/reports/reports.module')
          .then(m => m.ReportsModule)
      },
      { path: '', redirectTo: 'shipments', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LayoutRoutingModule {}
```

---

## 7. Auth Routing Module

**Location**: `src/app/features/login/auth-routing.module.ts`

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login.component';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
    data: {
      showHeader: false,
      showSidebar: false,
      showFooter: false
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule {}
```

---

## 8. Sample Feature Routing (Shipments)

**Location**: `src/app/features/shipments/shipments-routing.module.ts`

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ShipmentsListComponent } from './shipments-list/shipments-list.component';
import { ShipmentDetailComponent } from './shipment-detail/shipment-detail.component';
import { ShipmentCreateComponent } from './shipment-create/shipment-create.component';

const routes: Routes = [
  {
    path: '',
    component: ShipmentsListComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: 'create',
    component: ShipmentCreateComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: ':id',
    component: ShipmentDetailComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ShipmentsRoutingModule {}
```

---

## 9. Layout Module

**Location**: `src/app/layout/layout-module.ts`

```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';

import { Header } from './header/header';
import { Sidebar } from './sidebar/sidebar';
import { Footer } from './footer/footer';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { LayoutRoutingModule } from './layout-routing.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule,
    LayoutRoutingModule,
    // Standalone components
    MainLayoutComponent,
    Header,
    Sidebar,
    Footer
  ],
  exports: [
    MainLayoutComponent,
    Header,
    Sidebar,
    Footer
  ]
})
export class LayoutModule {}
```

---

## 10. Key Concepts

### Route Data Flow:
```
AppRoutingModule (login + layout)
  ↓
LayoutRoutingModule (parent + children with route data)
  ↓
MainLayoutComponent (reads route data)
  ↓
Conditionally renders Header/Sidebar/Footer based on data
```

### Dynamic Menu Flow:
```
Sidebar Component
  ↓
MenuService.getMenuItems()
  ↓
Renders *ngFor with dynamic menu items
  ↓
routerLink navigates to item.route
```

### Responsive Breakpoints:
```
Desktop (>768px): sidebar 250px, full layout visible
Tablet (600-768px): sidebar 200px, search hidden
Mobile (<600px): full-width sidebar, minimal header
```

---

## Testing the Implementation

### 1. Start the app:
```bash
npm start
```

### 2. Navigate to different routes:
- `/` → Shows layout with header, sidebar, footer
- `/login` → Shows login without layout components
- `/shipments`, `/vehicles`, `/customers`, `/reports` → Shows layout with specific content

### 3. Test responsive:
- Open browser DevTools (F12)
- Toggle device toolbar (Ctrl+Shift+M)
- Resize to test mobile breakpoints

### 4. Test sidebar toggle:
- Click menu button in header
- Sidebar should show/hide smoothly

---

## Integration with Backend

### Replace menu service mock with API:
```typescript
// In menu.service.ts
import { HttpClient } from '@angular/common/http';

constructor(private http: HttpClient) {}

getMenuItems(): Observable<MenuItem[]> {
  return this.http.get<MenuItem[]>('/api/menu-items');
}
```

### Add interceptor for authentication:
```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

---

## Material Icons Used

- `menu` - Sidebar toggle
- `account_circle` - Profile button
- `notifications` - Notifications
- `settings` - Settings
- `logout` - Logout
- `local_shipping` - Shipments
- `directions_car` - Vehicles
- `people` - Customers
- `assessment` - Reports
- `add` - Create/New button
- `visibility` - View button
- `edit` - Edit button
- `delete` - Delete button
- `download` - Download button
- `facebook` - Social media
- `public` - Web link
- `language` - Language
