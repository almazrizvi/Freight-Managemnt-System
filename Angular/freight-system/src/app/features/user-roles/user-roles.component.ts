import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-user-roles',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatDialogModule,
    RouterModule
  ],
  templateUrl: './user-roles.component.html',
  styleUrls: ['./user-roles.component.scss']
})
export class UserRolesComponent implements OnInit {
  displayedColumns: string[] = ['email', 'fullName', 'roles', 'actions'];
  users: any[] = [];
  isLoading = false;

  roles = [
    { id: 1, name: 'Admin', description: 'Full access to system' },
    { id: 2, name: 'Manager', description: 'Can manage users and records' },
    { id: 3, name: 'User', description: 'Can view and edit own records' },
    { id: 4, name: 'Viewer', description: 'Read-only access' }
  ];

  constructor(
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.isLoading = true;
    // TODO: Fetch user roles from backend
    setTimeout(() => {
      this.users = [];
      this.isLoading = false;
    }, 1000);
  }

  assignRole(user: any): void {
    this.snackBar.open('Role assigned successfully', 'Close', { duration: 3000 });
  }

  removeRole(user: any, role: any): void {
    this.snackBar.open('Role removed successfully', 'Close', { duration: 3000 });
  }

  goBack(): void {
    this.router.navigate(['/admin/users']);
  }
}
