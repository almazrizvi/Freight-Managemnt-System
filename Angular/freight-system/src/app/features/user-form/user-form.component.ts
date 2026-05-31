import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User } from '../../core/user.model';
import { UserService } from '../../core/user.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatSnackBarModule,
    MatIconModule,
    RouterModule
  ],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  userId?: string;
  userTypes = ['INTERNAL', 'CUSTOMER', 'DRIVER'];

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.userForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', Validators.required],
      passwordHash: ['', Validators.required],
      userType: ['INTERNAL', Validators.required],
      isActive: [true]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.userId = params['id'];
        this.loadUser();
      }
    });
  }

  loadUser(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.userService.getUserById(this.userId).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          email: user.email,
          fullName: user.fullName,
          userType: user.userType,
          isActive: user.isActive
        });
        if (this.isEditMode) {
          this.userForm.get('passwordHash')?.clearAsyncValidators();
          this.userForm.get('passwordHash')?.reset();
          this.userForm.get('passwordHash')?.clearValidators();
          this.userForm.get('passwordHash')?.updateValueAndValidity();
        }
        setTimeout(() => {
          this.isLoading = false;
        });
      },
      error: (error) => {
        console.error('Error loading user:', error);
        this.snackBar.open('Failed to load user', 'Close', { duration: 3000 });
        this.router.navigate(['/admin/users']);
        setTimeout(() => {
          this.isLoading = false;
        });
      }
    });
  }

  onSubmit(): void {
    if (!this.userForm.valid) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.isLoading = true;
    const userData: User = this.userForm.value;

    if (this.isEditMode && this.userId) {
      this.userService.updateUser(this.userId, userData).subscribe({
        next: () => {
          this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/admin/users']);
          setTimeout(() => {
            this.isLoading = false;
          });
        },
        error: (error) => {
          console.error('Error updating user:', error);
          this.snackBar.open(error.error?.message || 'Failed to update user', 'Close', { duration: 3000 });
          setTimeout(() => {
            this.isLoading = false;
          });
        }
      });
    } else {
      this.userService.createUser(userData).subscribe({
        next: () => {
          this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/admin/users']);
          setTimeout(() => {
            this.isLoading = false;
          });
        },
        error: (error) => {
          console.error('Error creating user:', error);
          this.snackBar.open(error.error?.message || 'Failed to create user', 'Close', { duration: 3000 });
          setTimeout(() => {
            this.isLoading = false;
          });
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/users']);
  }

  getPageTitle(): string {
    return this.isEditMode ? 'Edit User' : 'Create New User';
  }
}
