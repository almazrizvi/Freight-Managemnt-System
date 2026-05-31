import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { ACCESS_ACTIONS } from '../../core/access-catalog';
import { RbacAdminService, RoleDefinition, RolePermissionRow } from '../../core/rbac-admin.service';

@Component({
  selector: 'app-user-permissions',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule
  ],
  templateUrl: './user-permissions.component.html',
  styleUrls: ['./user-permissions.component.scss']
})
export class UserPermissionsComponent implements OnInit {
  displayedColumns: string[] = ['permission', 'VIEW', 'CREATE', 'UPDATE', 'DELETE', 'APPROVE'];
  permissions: RolePermissionRow[] = [];
  availableActions = ACCESS_ACTIONS;
  roles: RoleDefinition[] = [];
  selectedRoleCode = 'ADMIN';
  selectedUserId = '';
  isLoading = false;

  constructor(
    private rbacAdminService: RbacAdminService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.selectedUserId = this.route.snapshot.queryParamMap.get('user') ?? '';
    const requestedRole = this.route.snapshot.queryParamMap.get('role');
    if (requestedRole) {
      this.selectedRoleCode = requestedRole.toUpperCase();
    }

    this.rbacAdminService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
      }
    });

    this.loadPermissions();
  }

  loadPermissions(): void {
    this.isLoading = true;
    this.rbacAdminService.getRolePermissionMatrix(this.selectedRoleCode).subscribe({
      next: (matrix) => {
        this.permissions = matrix.permissions.map((permission) => ({
          ...permission,
          assignedActions: [...permission.assignedActions]
        }));
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load role permissions', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  onRoleChange(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        role: this.selectedRoleCode,
        user: this.selectedUserId || null
      },
      queryParamsHandling: 'merge'
    });
    this.loadPermissions();
  }

  hasAction(permission: RolePermissionRow, action: string): boolean {
    return permission.assignedActions.includes(action);
  }

  togglePermission(permission: RolePermissionRow, action: string, checked: boolean): void {
    const assignedActions = new Set(permission.assignedActions);
    if (checked) {
      assignedActions.add(action);
    } else {
      assignedActions.delete(action);
    }
    permission.assignedActions = Array.from(assignedActions);
  }

  savePermissions(): void {
    this.rbacAdminService.saveRolePermissionMatrix(this.selectedRoleCode, this.permissions).subscribe({
      next: () => {
        this.snackBar.open(`${this.selectedRoleCode} permissions saved`, 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Failed to save permissions', 'Close', { duration: 3000 });
      }
    });
  }

  resetPermissions(): void {
    this.loadPermissions();
    this.snackBar.open('Permissions reset', 'Close', { duration: 3000 });
  }

  goBack(): void {
    if (this.selectedUserId) {
      this.router.navigate(['/admin/users/roles'], {
        queryParams: {
          user: this.selectedUserId
        }
      });
      return;
    }

    this.router.navigate(['/admin/users']);
  }
}
