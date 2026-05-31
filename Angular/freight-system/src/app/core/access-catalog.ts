export type PermissionAction = 'VIEW' | 'CREATE' | 'UPDATE' | 'DELETE' | 'APPROVE';

export interface AccessModule {
  menuId: string;
  title: string;
  description: string;
  route?: string | null;
  icon: string;
  category: 'operations' | 'admin' | 'future';
}

export interface PermissionState {
  VIEW: boolean;
  CREATE: boolean;
  UPDATE: boolean;
  DELETE: boolean;
  APPROVE: boolean;
}

export interface RoleDefinition {
  roleCode: string;
  roleName: string;
  description: string;
  systemRole: boolean;
  active: boolean;
}

export const ACCESS_ACTIONS: PermissionAction[] = ['VIEW', 'CREATE', 'UPDATE', 'DELETE', 'APPROVE'];

export const ACCESS_MODULES: AccessModule[] = [
  {
    menuId: 'shipments',
    title: 'Shipments',
    description: 'Air waybills, shipment records, and milestone updates.',
    route: '/shipments',
    icon: 'local_shipping',
    category: 'operations'
  },
  {
    menuId: 'billing',
    title: 'Billing',
    description: 'Invoices, charges, and payment review for future billing rollout.',
    route: null,
    icon: 'receipt_long',
    category: 'future'
  },
  {
    menuId: 'vehicles',
    title: 'Vehicles',
    description: 'Vehicle availability, assignment, and maintenance visibility.',
    route: '/vehicles',
    icon: 'directions_car',
    category: 'operations'
  },
  {
    menuId: 'customers',
    title: 'Customers',
    description: 'Customer master data and account visibility.',
    route: '/customers',
    icon: 'groups',
    category: 'operations'
  },
  {
    menuId: 'reports',
    title: 'Reports',
    description: 'Operational and management reporting screens.',
    route: '/reports',
    icon: 'assessment',
    category: 'operations'
  },
  {
    menuId: 'admin_users',
    title: 'Users',
    description: 'User list and lifecycle management.',
    route: '/admin/users',
    icon: 'person',
    category: 'admin'
  },
  {
    menuId: 'admin_users_create',
    title: 'Create User',
    description: 'Create or edit application users.',
    route: '/admin/users/create',
    icon: 'person_add',
    category: 'admin'
  },
  {
    menuId: 'admin_roles',
    title: 'User Roles',
    description: 'Assign one or more roles to users.',
    route: '/admin/users/roles',
    icon: 'security',
    category: 'admin'
  },
  {
    menuId: 'admin_permissions',
    title: 'User Permissions',
    description: 'Configure read/write/module permissions by role.',
    route: '/admin/users/permissions',
    icon: 'vpn_key',
    category: 'admin'
  },
  {
    menuId: 'admin_activity',
    title: 'Operations Monitor',
    description: 'Monitor jobs and control start, pause, and stop actions.',
    route: '/admin/users/activity',
    icon: 'monitor_heart',
    category: 'admin'
  }
];

export const DEFAULT_ROLES: RoleDefinition[] = [
  {
    roleCode: 'ADMIN',
    roleName: 'Administrator',
    description: 'Full operational and administrative access.',
    systemRole: true,
    active: true
  },
  {
    roleCode: 'INTERNAL',
    roleName: 'Internal User',
    description: 'Default internal operations access.',
    systemRole: true,
    active: true
  },
  {
    roleCode: 'CUSTOMER',
    roleName: 'Customer User',
    description: 'Customer visibility into shipments and billing.',
    systemRole: true,
    active: true
  },
  {
    roleCode: 'DRIVER',
    roleName: 'Driver User',
    description: 'Driver visibility into assigned jobs and vehicles.',
    systemRole: true,
    active: true
  }
];

const buildPermissionState = (actions: PermissionAction[] = []): PermissionState => ({
  VIEW: actions.includes('VIEW'),
  CREATE: actions.includes('CREATE'),
  UPDATE: actions.includes('UPDATE'),
  DELETE: actions.includes('DELETE'),
  APPROVE: actions.includes('APPROVE')
});

export const DEFAULT_ROLE_PERMISSIONS: Record<string, Record<string, PermissionState>> = {
  ADMIN: Object.fromEntries(
    ACCESS_MODULES.map((module) => [module.menuId, buildPermissionState(ACCESS_ACTIONS)])
  ),
  INTERNAL: {
    shipments: buildPermissionState(['VIEW', 'CREATE', 'UPDATE', 'APPROVE']),
    billing: buildPermissionState(['VIEW']),
    vehicles: buildPermissionState(['VIEW', 'UPDATE']),
    customers: buildPermissionState(['VIEW', 'UPDATE']),
    reports: buildPermissionState(['VIEW']),
    admin_users: buildPermissionState(['VIEW']),
    admin_users_create: buildPermissionState(['VIEW', 'CREATE', 'UPDATE']),
    admin_roles: buildPermissionState(['VIEW']),
    admin_permissions: buildPermissionState(['VIEW']),
    admin_activity: buildPermissionState(['VIEW'])
  },
  CUSTOMER: {
    shipments: buildPermissionState(['VIEW', 'CREATE']),
    billing: buildPermissionState(['VIEW']),
    reports: buildPermissionState(['VIEW'])
  },
  DRIVER: {
    shipments: buildPermissionState(['VIEW', 'UPDATE']),
    vehicles: buildPermissionState(['VIEW']),
    admin_activity: buildPermissionState(['VIEW'])
  }
};

export const FULL_ACCESS_MENU_IDS = Array.from(
  new Set(
    ACCESS_MODULES.filter((module) => module.route)
      .map((module) => module.menuId)
      .concat('admin')
  )
);

export const FULL_ACCESS_AUTHORITIES = ACCESS_MODULES.flatMap((module) =>
  ACCESS_ACTIONS.map((action) => `${module.menuId}:${action.toLowerCase()}`)
);

export const DEFAULT_ROUTE_BY_MENU_ID: Record<string, string> = Object.fromEntries(
  ACCESS_MODULES.filter((module) => module.route).map((module) => [module.menuId, module.route as string])
);

export function deriveMenuIdsFromAuthorities(authorities: string[]): string[] {
  const menuIds = new Set<string>();
  for (const authority of authorities) {
    const [menuId] = authority.split(':');
    if (menuId) {
      menuIds.add(menuId);
      if (menuId.startsWith('admin_')) {
        menuIds.add('admin');
      }
    }
  }
  return Array.from(menuIds);
}

export function getDefaultRolesForUserType(userType?: string | null): string[] {
  switch ((userType ?? '').toUpperCase()) {
    case 'CUSTOMER':
      return ['CUSTOMER'];
    case 'DRIVER':
      return ['DRIVER'];
    default:
      return ['INTERNAL'];
  }
}

