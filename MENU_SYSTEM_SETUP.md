# Menu System Setup Guide

## Overview
The menu system has been implemented with both Java backend and Angular frontend components to dynamically fetch menu items from the database.

## Backend Setup (Java User Service)

### Files Created:
1. **Menu.java** - JPA Entity for menu items
   - Location: `src/main/java/com/freight/management/user_service/user/model/Menu.java`
   - Represents the menu database table

2. **MenuDto.java** - Data Transfer Object
   - Location: `src/main/java/com/freight/management/user_service/user/dto/MenuDto.java`
   - Used for API responses with hierarchical support

3. **MenuRepository.java** - Spring Data JPA Repository
   - Location: `src/main/java/com/freight/management/user_service/user/repository/MenuRepository.java`
   - Provides database queries for menu items

4. **MenuService.java** - Business Logic Service
   - Location: `src/main/java/com/freight/management/user_service/user/service/MenuService.java`
   - Handles menu retrieval and hierarchical structure building

5. **MenuController.java** - REST API Controller
   - Location: `src/main/java/com/freight/management/user_service/user/controller/MenuController.java`
   - Exposes menu endpoints

6. **menu_schema.sql** - Database Schema
   - Location: `src/main/resources/menu_schema.sql`
   - Contains table creation and sample data

### Database Setup:
1. Execute the SQL script to create the menu table:
   ```sql
   -- Run the menu_schema.sql file on your database
   ```

2. The table structure includes:
   - `menu_id` (Primary Key)
   - `parent_id` (Foreign Key for hierarchical structure)
   - `title` (Display name)
   - `angular_route` (Angular router path)
   - `display_order` (Order of display)
   - `icon` (Material icon name)
   - `level` (Nesting level)
   - `is_active` (Soft delete flag)
   - `created_at` & `updated_at` (Timestamps)

### API Endpoints:

1. **Get all active menus (hierarchical)**
   ```
   GET /api/menus
   ```
   Returns all active menu items in hierarchical structure

2. **Get all menus including inactive**
   ```
   GET /api/menus/all
   ```
   Admin endpoint to see all menus

3. **Get root menus only**
   ```
   GET /api/menus/root
   ```
   Returns only top-level menus

4. **Get menu children**
   ```
   GET /api/menus/{parentId}/children
   ```
   Returns children of a specific menu item

5. **Get specific menu**
   ```
   GET /api/menus/{menuId}
   ```
   Returns a single menu item

6. **Create menu**
   ```
   POST /api/menus
   Body: Menu object
   ```

7. **Update menu**
   ```
   PUT /api/menus/{menuId}
   Body: Menu object
   ```

8. **Delete menu**
   ```
   DELETE /api/menus/{menuId}
   ```

## Frontend Setup (Angular)

### Updated Files:

1. **menu.service.ts** - Menu Service
   - Location: `src/app/shared/services/menu.service.ts`
   - Now fetches from backend API at `http://localhost:9090/api/menus`
   - Falls back to default menus if API is unavailable

2. **sidebar.ts** - Sidebar Component
   - Updated to normalize menu items
   - Supports both `label` and `title` properties
   - Supports both `route` and `angularRoute` properties

3. **sidebar.html** - Sidebar Template
   - Updated to use conditional properties for better compatibility

### Configuration:

The Angular menu service is configured to call:
```
http://localhost:9090/api/menus
```

Make sure your user-service is running on port 9090, or update the `apiUrl` in `menu.service.ts`.

## Usage Example

### Create a Menu Item via API:
```json
POST /api/menus
{
  "menuId": "new_menu_001",
  "parentId": null,
  "title": "New Menu Item",
  "angularRoute": "/new-route",
  "displayOrder": 6,
  "icon": "dashboard",
  "level": 1,
  "isActive": true
}
```

### Create a Submenu Item:
```json
POST /api/menus
{
  "menuId": "new_submenu_001",
  "parentId": "admin",
  "title": "New Admin Feature",
  "angularRoute": "/admin/new-feature",
  "displayOrder": 6,
  "icon": "settings",
  "level": 2,
  "isActive": true
}
```

## Running the Application

1. Start the user-service:
   ```bash
   cd java/user-service
   mvn spring-boot:run
   ```

2. Start the Angular application:
   ```bash
   cd Angular/freight-system
   npm start
   ```

3. The sidebar will automatically fetch menus from the backend on initialization.

## Error Handling

- If the backend API is unavailable, the application falls back to default menus
- Check the browser console for warnings about failed API calls
- Ensure CORS is properly configured (already set to allow `http://localhost:4200`)

## Notes

- The menu hierarchy is built on the backend side for better performance
- The `display_order` field determines the position of menus
- Use `is_active` flag to hide menus without deleting them
- The API automatically sorts menus by `display_order`
