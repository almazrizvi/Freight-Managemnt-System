-- Create menu table
CREATE TABLE menu (
    menu_id        VARCHAR(50)   NOT NULL,   -- custom ID, not auto-generated
    parent_id      VARCHAR(50),              -- references another menu_id
    title          VARCHAR(100)  NOT NULL,   -- display name
    angular_route  VARCHAR(255),             -- Angular route/path
    display_order  INT           NOT NULL,   -- order under same parent
    icon           VARCHAR(100),             -- icon name or path
    level          INT,                      -- optional nesting level
    is_active      BOOLEAN       DEFAULT TRUE,
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (menu_id),
    FOREIGN KEY (parent_id) REFERENCES menu(menu_id)
);

-- Insert sample menu data
INSERT INTO menu (menu_id, parent_id, title, angular_route, display_order, icon, level, is_active) VALUES
('shipments', NULL, 'Shipments', '/shipments', 1, 'local_shipping', 1, TRUE),
('vehicles', NULL, 'Vehicles', '/vehicles', 2, 'directions_car', 1, TRUE),
('customers', NULL, 'Customers', '/customers', 3, 'people', 1, TRUE),
('reports', NULL, 'Reports', '/reports', 4, 'assessment', 1, TRUE),
('admin', NULL, 'Admin', NULL, 5, 'admin_panel_settings', 1, TRUE),
('admin_users', 'admin', 'User Management', '/admin/users', 1, 'person', 2, TRUE),
('admin_users_create', 'admin', 'Create User', '/admin/users/create', 2, 'person_add', 2, TRUE),
('admin_roles', 'admin', 'User Roles', '/admin/users/roles', 3, 'security', 2, TRUE),
('admin_activity', 'admin', 'User Activity', '/admin/users/activity', 4, 'history', 2, TRUE),
('admin_permissions', 'admin', 'User Permissions', '/admin/users/permissions', 5, 'vpn_key', 2, TRUE);

-- You can optionally create an index on parent_id for better query performance
CREATE INDEX idx_menu_parent_id ON menu(parent_id);
