import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-customer-detail',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterModule],
  template: `
    <div class="container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Customer Detail</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Customer detail component. Coming soon...</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-button [routerLink]="['/customers']">Back to List</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .container {
      padding: 16px;
    }
  `]
})
export class CustomerDetailComponent {}
