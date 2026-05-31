import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../core/user.model';

@Component({
  selector: 'app-user-delete-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Delete User</h2>
    <mat-dialog-content>
      <p>Are you sure you want to delete this user?</p>
      <p><strong>{{ user.email }}</strong> ({{ user.fullName }})</p>
      <p style="font-size: 12px; color: #666;">This action will soft-delete the user and cannot be undone.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="warn" (click)="onDelete()">Delete</button>
    </mat-dialog-actions>
  `
})
export class UserDeleteDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UserDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public user: User
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onDelete(): void {
    this.dialogRef.close(true);
  }
}
