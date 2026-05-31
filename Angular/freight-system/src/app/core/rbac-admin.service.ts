import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import {
  ACCESS_ACTIONS,
  ACCESS_MODULES,
  DEFAULT_ROLES,
  DEFAULT_ROLE_PERMISSIONS,
  PermissionState,
  RoleDefinition,
  deriveMenuIdsFromAuthorities,
  getDefaultRolesForUserType
} from './access-catalog';
import { User } from './user.model';
import { UserService } from './user.service';

export type { RoleDefinition } from './access-catalog';

export interface RolePermissionRow {
  menuId: string;
  title: string;
  description: string;
  route?: string | null;
  icon: string;
  availableActions: string[];
  assignedActions: string[];
}

export interface RolePermissionMatrix {
  roleCode: string;
  roleName: string;
  description: string;
  permissions: RolePermissionRow[];
}

export interface UserAccessSummary extends User {
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class RbacAdminService {
  private readonly apiBaseUrl = 'http://localhost:9010/api/users/access';
  private readonly httpHeaders = new HttpHeaders({
    'Content-Type': 'application/json'
  });
  private readonly userRoleKey = 'freight-user-roles';
  private readonly rolePermissionKey = 'freight-role-permissions';

  constructor(
    private http: HttpClient,
    private userService: UserService
  ) {}

  getRoles(): Observable<RoleDefinition[]> {
    return this.http.get<RoleDefinition[]>(`${this.apiBaseUrl}/roles`, { headers: this.httpHeaders }).pipe(
      catchError(() => of(DEFAULT_ROLES))
    );
  }

  getUserAccessList(): Observable<UserAccessSummary[]> {
    return this.http.get<UserAccessSummary[]>(`${this.apiBaseUrl}/users`, { headers: this.httpHeaders }).pipe(
      map((users) => users.map((user) => this.normalizeUserAccess(user))),
      catchError(() =>
        this.userService.getAllUsers().pipe(
          map((users) => users.map((user) => this.buildFallbackUserAccess(user)))
        )
      )
    );
  }

  getUserAccess(userId: string): Observable<UserAccessSummary> {
    return this.http.get<UserAccessSummary>(`${this.apiBaseUrl}/users/${userId}`, { headers: this.httpHeaders }).pipe(
      map((user) => this.normalizeUserAccess(user)),
      catchError(() =>
        this.userService.getUserById(userId).pipe(map((user) => this.buildFallbackUserAccess(user)))
      )
    );
  }

  assignRoles(userId: string, roleCodes: string[]): Observable<UserAccessSummary> {
    const normalizedRoleCodes = this.normalizeRoleCodes(roleCodes);
    return this.http
      .put<UserAccessSummary>(
        `${this.apiBaseUrl}/users/${userId}/roles`,
        { roleCodes: normalizedRoleCodes },
        { headers: this.httpHeaders }
      )
      .pipe(
        map((user) => this.normalizeUserAccess(user)),
        catchError(() =>
          this.userService.getUserById(userId).pipe(
            map((user) => {
              this.saveFallbackUserRoles(userId, normalizedRoleCodes);
              return this.buildFallbackUserAccess({ ...user, roleCodes: normalizedRoleCodes });
            })
          )
        )
      );
  }

  getRolePermissionMatrix(roleCode: string): Observable<RolePermissionMatrix> {
    return this.http
      .get<RolePermissionMatrix>(`${this.apiBaseUrl}/permissions/${roleCode}`, { headers: this.httpHeaders })
      .pipe(catchError(() => of(this.buildFallbackPermissionMatrix(roleCode))));
  }

  saveRolePermissionMatrix(roleCode: string, permissions: RolePermissionRow[]): Observable<RolePermissionMatrix> {
    const normalizedPermissions = permissions.map((permission) => ({
      menuId: permission.menuId,
      actionCodes: permission.assignedActions
    }));

    return this.http
      .put<RolePermissionMatrix>(
        `${this.apiBaseUrl}/permissions/${roleCode}`,
        { permissions: normalizedPermissions },
        { headers: this.httpHeaders }
      )
      .pipe(
        catchError(() => {
          this.saveFallbackRolePermissions(roleCode, permissions);
          return of(this.buildFallbackPermissionMatrix(roleCode));
        })
      );
  }

  getPermissionSummary(roleCodes: string[]): { authorityCount: number; moduleCount: number; menuIds: string[] } {
    const authorities = this.getAuthoritiesForRoles(roleCodes);
    return {
      authorityCount: authorities.length,
      moduleCount: deriveMenuIdsFromAuthorities(authorities).filter((menuId) => menuId !== 'admin').length,
      menuIds: deriveMenuIdsFromAuthorities(authorities)
    };
  }

  getAuthoritiesForRoles(roleCodes: string[]): string[] {
    const storedPermissions = this.readFallbackRolePermissions();
    const authorities = new Set<string>();

    for (const roleCode of this.normalizeRoleCodes(roleCodes)) {
      const rolePermissions = storedPermissions[roleCode] ?? DEFAULT_ROLE_PERMISSIONS[roleCode] ?? {};
      Object.entries(rolePermissions).forEach(([menuId, state]) => {
        ACCESS_ACTIONS.forEach((action) => {
          if (state[action]) {
            authorities.add(`${menuId}:${action.toLowerCase()}`);
          }
        });
      });
    }

    return Array.from(authorities).sort();
  }

  private buildFallbackUserAccess(user: User): UserAccessSummary {
    const roleCodes =
      user.roleCodes && user.roleCodes.length > 0
        ? this.normalizeRoleCodes(user.roleCodes)
        : this.readFallbackUserRoles()[user.id ?? user.email] ?? getDefaultRolesForUserType(user.userType);
    const authorities = this.getAuthoritiesForRoles(roleCodes);

    return {
      ...user,
      userId: user.id ?? user.email,
      id: user.id ?? user.email,
      roleCodes,
      authorities,
      menuIds: deriveMenuIdsFromAuthorities(authorities)
    };
  }

  private normalizeUserAccess(user: UserAccessSummary): UserAccessSummary {
    return {
      ...user,
      id: user.id ?? user.userId
    };
  }

  private buildFallbackPermissionMatrix(roleCode: string): RolePermissionMatrix {
    const normalizedRoleCode = roleCode.toUpperCase();
    const roleDefinition =
      DEFAULT_ROLES.find((role) => role.roleCode === normalizedRoleCode) ??
      ({
        roleCode: normalizedRoleCode,
        roleName: normalizedRoleCode,
        description: 'Custom role',
        systemRole: false,
        active: true
      } satisfies RoleDefinition);

    const storedPermissions = this.readFallbackRolePermissions();
    const rolePermissions = storedPermissions[normalizedRoleCode] ?? DEFAULT_ROLE_PERMISSIONS[normalizedRoleCode] ?? {};

    return {
      roleCode: normalizedRoleCode,
      roleName: roleDefinition.roleName,
      description: roleDefinition.description,
      permissions: ACCESS_MODULES.map((module) => ({
        menuId: module.menuId,
        title: module.title,
        description: module.description,
        route: module.route,
        icon: module.icon,
        availableActions: [...ACCESS_ACTIONS],
        assignedActions: ACCESS_ACTIONS.filter((action) => rolePermissions[module.menuId]?.[action])
      }))
    };
  }

  private saveFallbackUserRoles(userId: string, roleCodes: string[]): void {
    const stored = this.readFallbackUserRoles();
    stored[userId] = this.normalizeRoleCodes(roleCodes);
    localStorage.setItem(this.userRoleKey, JSON.stringify(stored));
  }

  private readFallbackUserRoles(): Record<string, string[]> {
    const raw = localStorage.getItem(this.userRoleKey);
    return raw ? (JSON.parse(raw) as Record<string, string[]>) : {};
  }

  private saveFallbackRolePermissions(roleCode: string, permissions: RolePermissionRow[]): void {
    const stored = this.readFallbackRolePermissions();
    stored[roleCode.toUpperCase()] = Object.fromEntries(
      permissions.map((permission) => [
        permission.menuId,
        this.buildPermissionState(permission.assignedActions)
      ])
    );
    localStorage.setItem(this.rolePermissionKey, JSON.stringify(stored));
  }

  private readFallbackRolePermissions(): Record<string, Record<string, PermissionState>> {
    const raw = localStorage.getItem(this.rolePermissionKey);
    return raw ? (JSON.parse(raw) as Record<string, Record<string, PermissionState>>) : {};
  }

  private normalizeRoleCodes(roleCodes: string[]): string[] {
    return Array.from(
      new Set(
        roleCodes
          .filter(Boolean)
          .map((roleCode) => roleCode.trim().toUpperCase())
          .filter((roleCode) => roleCode.length > 0)
      )
    );
  }

  private buildPermissionState(assignedActions: string[]): PermissionState {
    return {
      VIEW: assignedActions.includes('VIEW'),
      CREATE: assignedActions.includes('CREATE'),
      UPDATE: assignedActions.includes('UPDATE'),
      DELETE: assignedActions.includes('DELETE'),
      APPROVE: assignedActions.includes('APPROVE')
    };
  }
}
