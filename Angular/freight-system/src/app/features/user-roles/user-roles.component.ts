import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RbacAdminService, RoleDefinition, UserAccessSummary } from '../../core/rbac-admin.service';

@Component({
  selector: 'app-user-roles',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule
  ],
  templateUrl: './user-roles.component.html',
  styleUrls: ['./user-roles.component.scss']
})
export class UserRolesComponent implements OnInit {
  displayedColumns: string[] = ['email', 'fullName', 'userType', 'roles', 'permissions', 'actions'];
  users: UserAccessSummary[] = [];
  roles: RoleDefinition[] = [];
  isLoading = false;
  selectedUserId = '';

  constructor(
    private rbacAdminService: RbacAdminService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.selectedUserId = this.route.snapshot.queryParamMap.get('user') ?? '';
    this.loadRoles();
  }

  loadRoles(): void {
    this.isLoading = true;

    this.rbacAdminService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
      }
    });

    this.rbacAdminService.getUserAccessList().subscribe({
      next: (users) => {
        this.users = users.map((user) => ({
          ...user,
          roleCodes: [...(user.roleCodes ?? [])]
        }));
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load user roles', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  saveRoles(user: UserAccessSummary): void {
    this.rbacAdminService.assignRoles(user.userId, user.roleCodes ?? []).subscribe({
      next: (updatedUser) => {
        Object.assign(user, updatedUser);
        this.snackBar.open(`Roles saved for ${user.fullName}`, 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Failed to save roles', 'Close', { duration: 3000 });
      }
    });
  }

  editPermissions(user: UserAccessSummary): void {
    const preferredRole = user.roleCodes?.[0] ?? 'INTERNAL';
    this.router.navigate(['/admin/users/permissions'], {
      queryParams: {
        role: preferredRole,
        user: user.userId
      }
    });
  }

  getPermissionSummary(user: UserAccessSummary): string {
    const summary = this.rbacAdminService.getPermissionSummary(user.roleCodes ?? []);
    return `${summary.moduleCount} modules / ${summary.authorityCount} actions`;
  }

  isSelectedUser(user: UserAccessSummary): boolean {
    return this.selectedUserId === user.userId;
  }

  goBack(): void {
    this.router.navigate(['/admin/users']);
  }
}
