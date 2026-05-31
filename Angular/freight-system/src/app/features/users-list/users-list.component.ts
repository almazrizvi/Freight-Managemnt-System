import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RouterModule, Router } from '@angular/router';
import { User } from '../../core/user.model';
import { UserService } from '../../core/user.service';
import { UserDeleteDialogComponent } from './user-delete-dialog/user-delete-dialog.component';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    RouterModule
  ],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  displayedColumns: string[] = ['email', 'fullName', 'userType', 'isActive', 'createdAt', 'actions'];
  isLoading = false;
  searchQuery = '';
  selectedUserType = '';
  userTypes = ['', 'INTERNAL', 'CUSTOMER', 'DRIVER'];

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.applyFilters();
        setTimeout(() => {
          this.isLoading = false;
        });
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
        setTimeout(() => {
          this.isLoading = false;
        });
      }
    });
  }

  onSearchChange(): void {
    if (this.searchQuery.length >= 2 || this.searchQuery.length === 0) {
      this.applyFilters();
    }
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let filtered = [...this.users];

    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(user =>
        user.email.toLowerCase().includes(query) ||
        user.fullName.toLowerCase().includes(query)
      );
    }

    // Apply user type filter
    if (this.selectedUserType) {
      filtered = filtered.filter(user => user.userType === this.selectedUserType);
    }

    this.filteredUsers = filtered;
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedUserType = '';
    this.filteredUsers = [...this.users];
  }

  editUser(user: User): void {
    this.router.navigate(['/admin/users', user.id, 'edit']);
  }

  deleteUser(user: User): void {
    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      width: '400px',
      data: user
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.performDelete(user);
      }
    });
  }

  private performDelete(user: User): void {
    if (!user.id) return;
    
    const currentUserId = ''; // TODO: Get from auth service
    this.userService.deleteUser(user.id, currentUserId).subscribe({
      next: () => {
        this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
        this.loadUsers();
      },
      error: (error) => {
        console.error('Error deleting user:', error);
        this.snackBar.open('Failed to delete user', 'Close', { duration: 3000 });
      }
    });
  }

  toggleStatus(user: User): void {
    if (!user.id) return;
    
    const newStatus = !user.isActive;
    this.userService.toggleUserStatus(user.id, newStatus).subscribe({
      next: () => {
        setTimeout(() => {
          this.snackBar.open(`User ${newStatus ? 'activated' : 'deactivated'}`, 'Close', { duration: 3000 });
          this.loadUsers();
        });
      },
      error: (error) => {
        console.error('Error updating user status:', error);
        this.snackBar.open('Failed to update user status', 'Close', { duration: 3000 });
      }
    });
  }

  createNewUser(): void {
    this.router.navigate(['/admin/users/create']);
  }
}
