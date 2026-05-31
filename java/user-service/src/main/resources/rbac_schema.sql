CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS app_role (
    app_role_id UUID PRIMARY KEY,
    role_code VARCHAR(100) NOT NULL UNIQUE,
    role_name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    is_system_role BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_action (
    app_action_id UUID PRIMARY KEY,
    action_code VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_role (
    user_role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    app_role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (app_role_id) REFERENCES app_role(app_role_id),
    CONSTRAINT uk_user_role_user_role UNIQUE (user_id, app_role_id)
);

CREATE TABLE IF NOT EXISTS role_menu_action (
    role_menu_action_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    app_role_id UUID NOT NULL,
    menu_id VARCHAR(50) NOT NULL,
    app_action_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_menu_action_role FOREIGN KEY (app_role_id) REFERENCES app_role(app_role_id),
    CONSTRAINT fk_role_menu_action_menu FOREIGN KEY (menu_id) REFERENCES menu(menu_id),
    CONSTRAINT fk_role_menu_action_action FOREIGN KEY (app_action_id) REFERENCES app_action(app_action_id),
    CONSTRAINT uk_role_menu_action UNIQUE (app_role_id, menu_id, app_action_id)
);

CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_role_menu_action_role_id ON role_menu_action(app_role_id);
CREATE INDEX IF NOT EXISTS idx_role_menu_action_menu_id ON role_menu_action(menu_id);

INSERT INTO app_role (app_role_id, role_code, role_name, description, is_system_role, is_active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'ADMIN', 'Administrator', 'Full access to administrative and operational screens.', TRUE, TRUE),
    ('22222222-2222-2222-2222-222222222222', 'INTERNAL', 'Internal User', 'Default role for internal operations users.', TRUE, TRUE),
    ('33333333-3333-3333-3333-333333333333', 'CUSTOMER', 'Customer User', 'Default role for customer portal users.', TRUE, TRUE),
    ('44444444-4444-4444-4444-444444444444', 'DRIVER', 'Driver User', 'Default role for drivers.', TRUE, TRUE)
ON CONFLICT (role_code) DO NOTHING;

INSERT INTO app_action (app_action_id, action_code, display_name, description, is_active)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'VIEW', 'View', 'Allows reading and opening a screen.', TRUE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'CREATE', 'Create', 'Allows creating records from a screen.', TRUE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'UPDATE', 'Update', 'Allows editing records from a screen.', TRUE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'DELETE', 'Delete', 'Allows deleting records from a screen.', TRUE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'APPROVE', 'Approve', 'Allows approval actions from a screen.', TRUE)
ON CONFLICT (action_code) DO NOTHING;

INSERT INTO role_menu_action (app_role_id, menu_id, app_action_id)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('11111111-1111-1111-1111-111111111111', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('11111111-1111-1111-1111-111111111111', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4'),
    ('11111111-1111-1111-1111-111111111111', 'vehicles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'vehicles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('11111111-1111-1111-1111-111111111111', 'vehicles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('11111111-1111-1111-1111-111111111111', 'customers', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'customers', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('11111111-1111-1111-1111-111111111111', 'customers', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('11111111-1111-1111-1111-111111111111', 'reports', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users_create', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'admin_users_create', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('11111111-1111-1111-1111-111111111111', 'admin_roles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'admin_permissions', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('11111111-1111-1111-1111-111111111111', 'admin_activity', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('22222222-2222-2222-2222-222222222222', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('22222222-2222-2222-2222-222222222222', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('22222222-2222-2222-2222-222222222222', 'vehicles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('22222222-2222-2222-2222-222222222222', 'customers', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('22222222-2222-2222-2222-222222222222', 'reports', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('33333333-3333-3333-3333-333333333333', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('44444444-4444-4444-4444-444444444444', 'shipments', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
    ('44444444-4444-4444-4444-444444444444', 'vehicles', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1')
ON CONFLICT ON CONSTRAINT uk_role_menu_action DO NOTHING;

INSERT INTO user_role (user_id, app_role_id)
SELECT u.id,
       CASE UPPER(COALESCE(u.user_type, 'INTERNAL'))
           WHEN 'CUSTOMER' THEN '33333333-3333-3333-3333-333333333333'::UUID
           WHEN 'DRIVER' THEN '44444444-4444-4444-4444-444444444444'::UUID
           ELSE '22222222-2222-2222-2222-222222222222'::UUID
       END
FROM app_user u
WHERE NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.user_id = u.id
)
ON CONFLICT ON CONSTRAINT uk_user_role_user_role DO NOTHING;
