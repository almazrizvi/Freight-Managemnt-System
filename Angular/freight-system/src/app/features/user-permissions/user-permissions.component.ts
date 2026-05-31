import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-user-permissions',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatSnackBarModule,
    RouterModule
  ],
  templateUrl: './user-permissions.component.html',
  styleUrls: ['./user-permissions.component.scss']
})
export class UserPermissionsComponent implements OnInit {
  displayedColumns: string[] = ['permission', 'read', 'write', 'delete'];
  permissions: any[] = [];
  isLoading = false;

  permissionsList = [
    { id: 1, name: 'Users', description: 'Manage user accounts' },
    { id: 2, name: 'Shipments', description: 'Manage shipments' },
    { id: 3, name: 'Vehicles', description: 'Manage vehicles' },
    { id: 4, name: 'Customers', description: 'Manage customers' },
    { id: 5, name: 'Reports', description: 'Generate reports' },
    { id: 6, name: 'Settings', description: 'System settings' }
  ];

  constructor(
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPermissions();
  }

  loadPermissions(): void {
    this.isLoading = true;
    // TODO: Fetch permissions from backend
    setTimeout(() => {
      this.permissions = this.permissionsList.map(p => ({
        ...p,
        read: false,
        write: false,
        delete: false
      }));
      this.isLoading = false;
    }, 1000);
  }

  savePermissions(): void {
    // TODO: Save permissions to backend
    this.snackBar.open('Permissions saved successfully', 'Close', { duration: 3000 });
  }

  resetPermissions(): void {
    this.loadPermissions();
    this.snackBar.open('Permissions reset', 'Close', { duration: 3000 });
  }

  goBack(): void {
    this.router.navigate(['/admin/users']);
  }
}
