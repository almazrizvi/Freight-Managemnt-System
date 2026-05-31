import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-vehicle-detail',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterModule],
  template: `
    <div class="container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Vehicle Detail</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Vehicle detail component. Coming soon...</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-button [routerLink]="['/vehicles']">Back to List</button>
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
export class VehicleDetailComponent {}
