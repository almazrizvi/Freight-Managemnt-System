import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-user-activity',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    RouterModule
  ],
  templateUrl: './user-activity.component.html',
  styleUrls: ['./user-activity.component.scss']
})
export class UserActivityComponent implements OnInit {
  displayedColumns: string[] = ['user', 'action', 'timestamp', 'details'];
  activities: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadActivity();
  }

  loadActivity(): void {
    this.isLoading = true;
    // TODO: Fetch user activity log from backend
    setTimeout(() => {
      this.activities = [];
      this.isLoading = false;
    }, 1000);
  }

  exportActivity(): void {
    // TODO: Implement export functionality
  }

  goBack(): void {
    this.router.navigate(['/admin/users']);
  }
}
