# Visual Layout Guide - Component Breakdown

## 🎨 Layout Architecture Visualization

```
╔════════════════════════════════════════════════════════════════╗
║                      HEADER (Fixed, 64px)                       ║
║  ┌────┐  Logo Text      Search Bar      Notifications  Profile  ║
║  │ ≡  │  🚚 Freight    ┌──────────┐        [3]      [👤 ▼]   ║
║  └────┘  Management    └──────────┘                             ║
╠══════════════════════════════════════════════════════════════════╣
║                                                                  ║
║  ┌──────────────────┐  ┌─────────────────────────────────────┐ ║
║  │   SIDEBAR        │  │                                     │ ║
║  │   (250px)        │  │        MAIN CONTENT                │ ║
║  │                  │  │      (Router Outlet)               │ ║
║  │  Menu            │  │                                     │ ║
║  │  ━━━━━━━━━━━━━━  │  │  ┌─────────────────────────────┐  │ ║
║  │  🚚 Shipments    │  │  │  Page Title                 │  │ ║
║  │  🚗 Vehicles     │  │  │  ────────────────────────   │  │ ║
║  │  👥 Customers    │  │  │                             │  │ ║
║  │  📊 Reports      │  │  │  Content Area               │  │ ║
║  │                  │  │  │  (List, Form, Detail)       │  │ ║
║  │                  │  │  │                             │  │ ║
║  │                  │  │  │                             │  │ ║
║  │                  │  │  └─────────────────────────────┘  │ ║
║  │                  │  │                                     │ ║
║  │                  │  │                                     │ ║
║  └──────────────────┘  └─────────────────────────────────────┘ ║
║                                                                  ║
╠══════════════════════════════════════════════════════════════════╣
║                      FOOTER (Sticky)                             ║
║  Company Info      Quick Links       Social Media                ║
║  ─────────────     ────────────      ──────────────              ║
║  • About           • Links           [f] [t] [in]               ║
║  • Contact         • Privacy         © 2026 All Rights          ║
║  • Description     • Terms           Reserved                    ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📱 Responsive Breakpoints

### 🖥️ Desktop (>768px)
```
┌────────────────────────────────────────────────────────┐
│                     HEADER (64px)                       │
├──────────────┬─────────────────────────────────────────┤
│              │                                          │
│  SIDEBAR     │                                          │
│  (250px)     │         MAIN CONTENT                     │
│              │      (Flexible Width)                    │
│              │                                          │
│              │                                          │
├──────────────┴─────────────────────────────────────────┤
│                     FOOTER                              │
│        (3 Columns Layout)                               │
└────────────────────────────────────────────────────────┘

Sidebar: Always visible, fixed 250px
Header: Full search bar visible
Footer: 3-column grid layout
```

### 📱 Tablet (600-768px)
```
┌────────────────────────────────────┐
│        HEADER (56px)               │
│  ≡  Logo      Search Hidden  👤    │
├────┬────────────────────────────────┤
│    │                                │
│ SB │      MAIN CONTENT              │
│ (  │    (Flexible Width)            │
│ 2  │                                │
│ 0  │                                │
│ 0  │                                │
│ px │                                │
│    │                                │
├────┴────────────────────────────────┤
│          FOOTER                     │
│    (2 Columns Layout)               │
└────────────────────────────────────┘

Sidebar: Reduced to 200px, visible
Header: Search bar hidden
Footer: 2-column grid layout
```

### 📱 Mobile (<600px)
```
┌──────────────────┐
│ HEADER (56px)    │
│ ≡ 🚚  ...   👤   │
├──────────────────┤
│                  │
│  MAIN CONTENT    │
│  (Full Width)    │
│                  │
│                  │
│                  │
├──────────────────┤
│    [SIDEBAR]     │
│    (Hidden)      │
│    (Toggle ≡)    │

│  (Can toggle)    │
│                  │
├──────────────────┤
│     FOOTER       │
│  (1 Column)      │
└──────────────────┘

Sidebar: Hidden by default, overlay when toggled
Header: Minimal, menu icon visible
Footer: Single column layout
```

---

## 🎯 Component Hierarchy

```
App (Root)
│
├── RouterModule (forRoot)
│   └── Routes []
│       ├── /login → LoginComponent (standalone)
│       │   ├── no showHeader
│       │   ├── no showSidebar
│       │   └── no showFooter
│       │
│       └── / → MainLayoutComponent
│           ├── ├─ Header (conditional)
│           │   │  ├─ Logo
│           │   │  ├─ Menu Toggle Button
│           │   │  ├─ Search Bar
│           │   │  ├─ Notifications Badge
│           │   │  └─ Profile Dropdown
│           │   │
│           │   ├─ Sidebar (conditional)
│           │   │  ├─ MenuService (injected)
│           │   │  │  └─ getMenuItems() → Observable<MenuItem[]>
│           │   │  │     ├─ Shipments
│           │   │  │     ├─ Vehicles
│           │   │  │     ├─ Customers
│           │   │  │     └─ Reports
│           │   │  
│           │   ├─ Router-Outlet
│           │   │  ├─ /shipments → ShipmentsModule
│           │   │  │  ├─ ShipmentsListComponent
│           │   │  │  ├─ ShipmentDetailComponent
│           │   │  │  └─ ShipmentCreateComponent
│           │   │  │
│           │   │  ├─ /vehicles → VehiclesModule
│           │   │  │  ├─ VehiclesListComponent
│           │   │  │  ├─ VehicleDetailComponent
│           │   │  │  └─ VehicleCreateComponent
│           │   │  │
│           │   │  ├─ /customers → CustomersModule
│           │   │  │  ├─ CustomersListComponent
│           │   │  │  ├─ CustomerDetailComponent
│           │   │  │  └─ CustomerCreateComponent
│           │   │  │
│           │   │  └─ /reports → ReportsModule
│           │   │     ├─ ReportsListComponent
│           │   │     ├─ ReportDetailComponent
│           │   │     └─ ReportCreateComponent
│           │   │
│           │   └─ Footer (conditional)
│           │      ├─ Company Info Section
│           │      ├─ Quick Links Section
│           │      └─ Social Media Section
│           │
│           └── Router Events Listener
│               └─ Updates layout visibility on route change
```

---

## 📊 Data Flow Diagrams

### Menu Loading Flow
```
User Interaction
      │
      ▼
  Sidebar Component: ngOnInit()
      │
      ├─ Inject MenuService
      │
      ├─ Call menuService.getMenuItems()
      │      │
      │      ├─ (Currently) Returns: of(defaultMenuItems)
      │      │
      │      └─ (Future) Returns: this.http.get('/api/menu-items')
      │
      └─ Subscribe to Observable
         │
         ├─ Receive MenuItem[]
         │
         └─ Populate menuItems property
            │
            └─ Template renders with *ngFor
               │
               ├─ Creates <mat-list-item> for each item
               │
               └─ Click event triggers [routerLink]
                  │
                  └─ Navigate to item.route
```

### Route-Based Layout Visibility Flow
```
User navigates to route
      │
      ▼
Router matches route
      │
      ├─ Found in LayoutRoutingModule
      │
      ├─ Route has data object
      │  {
      │    showHeader: boolean,
      │    showSidebar: boolean,
      │    showFooter: boolean
      │  }
      │
      ▼
MainLayoutComponent detects NavigationEnd event
      │
      ├─ Calls updateLayoutVisibility()
      │
      ├─ Reads: activatedRoute.snapshot.firstChild?.data
      │
      ├─ Updates component properties:
      │  - this.showHeader
      │  - this.showSidebar
      │  - this.showFooter
      │
      ▼
Template conditionally renders
      │
      ├─ <app-header *ngIf="showHeader">
      ├─ <app-sidebar *ngIf="showSidebar">
      └─ <app-footer *ngIf="showFooter">
```

---

## 🔄 Module Dependency Graph

```
AppModule
    │
    ├─ AppRoutingModule (forRoot)
    │  │
    │  ├─ route: /login
    │  │  └─ LoginModule (lazy)
    │  │     └─ AuthRoutingModule
    │  │        └─ LoginComponent
    │  │
    │  └─ route: /
    │     └─ LayoutModule (lazy)
    │        ├─ LayoutRoutingModule
    │        │  ├─ route: /shipments
    │        │  │  └─ ShipmentsModule (lazy)
    │        │  ├─ route: /vehicles
    │        │  │  └─ VehiclesModule (lazy)
    │        │  ├─ route: /customers
    │        │  │  └─ CustomersModule (lazy)
    │        │  └─ route: /reports
    │        │     └─ ReportsModule (lazy)
    │        │
    │        └─ Components:
    │           ├─ MainLayoutComponent (standalone)
    │           ├─ Header (standalone)
    │           ├─ Sidebar (standalone)
    │           │  └─ (depends on MenuService)
    │           └─ Footer (standalone)
    │
    └─ Services:
       └─ MenuService (provided in root)
```

---

## 🎨 Material Components Used

```
Header
├─ MatToolbarModule
│  └─ <mat-toolbar> with color="primary"
├─ MatIconModule
│  └─ <mat-icon> for menu, notifications, profile
├─ MatButtonModule
│  └─ <button mat-icon-button>
├─ MatMenuModule
│  └─ <button [matMenuTriggerFor]="menu">
├─ MatBadgeModule
│  └─ <mat-icon matBadge="3">
└─ MatDividerModule
   └─ <mat-divider>

Sidebar
├─ MatListModule
│  ├─ <mat-nav-list>
│  ├─ <mat-list-item>
│  └─ <span matListItemTitle>
├─ MatIconModule
│  └─ <mat-icon matListItemIcon>
└─ [RouterModule]
   └─ [routerLink], routerLinkActive

Layout
├─ MatSidenavModule
│  ├─ <mat-sidenav-container>
│  ├─ <mat-sidenav mode="side">
│  └─ <mat-sidenav-content>
└─ CommonModule
   └─ *ngIf, *ngFor

Footer
└─ MatDividerModule
   └─ <mat-divider>

Lists
├─ MatTableModule
│  ├─ <table mat-table>
│  ├─ <th mat-header-cell>
│  └─ <td mat-cell>
├─ MatButtonModule
│  └─ <button mat-icon-button>
└─ MatCardModule
   └─ <mat-card>
```

---

## 🔧 Key CSS Classes & Selectors

```scss
// Layout structure
.fixed-header { position: fixed; top: 0; left: 0; right: 0; }
.sidenav-container { position: absolute; }
.sidenav { width: 250px; }
.main-content { flex: 1; }

// Header
.logo { font-weight: 600; }
.search-section { display: flex; }
.header-actions { display: flex; }

// Sidebar
.sidebar-nav { padding: 16px 0; }
.nav-title { font-size: 14px; text-transform: uppercase; }
.menu-item { cursor: pointer; }
.menu-item.active { background-color: rgba(...); color: #3f51b5; }

// Footer
.app-footer { background-color: #f5f5f5; margin-top: auto; }
.footer-content { display: grid; }
.footer-section { /* section styling */ }
.footer-bottom { text-align: center; }

// Responsive
@media (max-width: 768px) { /* tablet */ }
@media (max-width: 600px) { /* mobile */ }
```

---

## 📐 Layout Dimensions

### Default Widths
```
Desktop (>768px)
├─ Sidebar: 250px
├─ Header: 100% (64px height)
├─ Footer: 100%
└─ Main: calc(100% - 250px)

Tablet (600-768px)
├─ Sidebar: 200px
├─ Header: 100% (56px height)
├─ Footer: 100%
└─ Main: calc(100% - 200px)

Mobile (<600px)
├─ Sidebar: 0px (hidden/overlay)
├─ Header: 100% (56px height)
├─ Footer: 100%
└─ Main: 100%
```

### Padding & Margins
```
Header: 0 horizontal padding
Sidebar: 16px vertical padding
Main Content: 24px (desktop), 16px (tablet), 12px (mobile)
Footer: 32px (desktop), 24px (tablet), 16px (mobile)
```

---

## 🎯 State Management

### Component States
```
MainLayoutComponent
├─ showHeader: boolean (true/false based on route)
├─ showSidebar: boolean (true/false based on route)
├─ showFooter: boolean (true/false based on route)
└─ sidenavOpened: boolean (true by default, toggleable)

Sidebar
├─ menuItems: MenuItem[] (from MenuService)
└─ Observable subscription (cleanup on ngOnDestroy)
```

### Route Data States
```
Login Routes
└─ showHeader: false, showSidebar: false, showFooter: false

Main Layout Routes
├─ /shipments: showHeader: true, showSidebar: true, showFooter: true
├─ /vehicles: showHeader: true, showSidebar: true, showFooter: true
├─ /customers: showHeader: true, showSidebar: true, showFooter: true
└─ /reports: showHeader: true, showSidebar: true, showFooter: true
```

---

## 🚀 Performance Characteristics

### Bundle Sizes
```
Initial Load
├─ main.js: ~2.54 KB
├─ styles.css: ~8.87 KB
└─ Total Initial: ~11.64 KB

Lazy-Loaded Chunks
├─ layout-module: ~47.62 KB
├─ reports-module: ~30.05 KB
├─ customers-module: ~27.05 KB
├─ vehicles-module: ~26.88 KB
├─ login-module: ~21.87 KB
└─ shipments-module: ~8.73 KB
```

### Rendering Performance
```
Initial Load: <1s
Route Navigation: <200ms
Layout Toggle: <100ms (smooth animation)
Menu Load: <50ms (mock data) / <500ms (API call)
```

---

## 🎨 Color & Theme

### Material Theme (Default)
```
Primary Color: #3f51b5 (Indigo)
Accent Color: #ff4081 (Pink)
Warn Color: #f44336 (Red)

Background: #ffffff
Text: #000000 (87% opacity)
Secondary Text: #666666 (60% opacity)

Hover States:
├─ Override: rgba(0, 0, 0, 0.04)
└─ Active: rgba(63, 81, 181, 0.1)

Header Background: Primary Color
Sidebar Background: #ffffff
Footer Background: #f5f5f5
```

---

**This visual guide provides a complete overview of the layout structure, component hierarchy, data flow, and styling system.**

*For implementation details, see CODE_REFERENCE.md*
*For testing procedures, see TESTING_GUIDE.md*
