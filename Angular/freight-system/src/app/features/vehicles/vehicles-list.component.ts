import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-vehicles-list',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatPaginatorModule,
    MatSortModule,
    MatToolbarModule,
    RouterModule
  ],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <h2>Vehicles</h2>
      <span class="spacer"></span>
      <button mat-raised-button color="accent" [routerLink]="['/vehicles', 'create']">
        <mat-icon>add</mat-icon>
        New Vehicle
      </button>
    </mat-toolbar>

    <div class="container">
      <mat-card>
        <mat-card-content>
          <p>Vehicles list component. Coming soon...</p>
          <table mat-table [dataSource]="vehicleData" class="vehicles-table">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let element">{{ element.id }}</td>
            </ng-container>

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Vehicle Name</th>
              <td mat-cell *matCellDef="let element">{{ element.name }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let element">
                <button mat-icon-button [routerLink]="['/vehicles', element.id]" title="View">
                  <mat-icon>visibility</mat-icon>
                </button>
                <button mat-icon-button title="Edit">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button title="Delete">
                  <mat-icon>delete</mat-icon>
                </button>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .toolbar {
      margin-bottom: 20px;
    }
    .spacer {
      flex: 1 1 auto;
    }
    .container {
      padding: 16px;
    }
    .vehicles-table {
      width: 100%;
    }
  `]
})
export class VehiclesListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'actions'];
  vehicleData: any[] = [];

  ngOnInit(): void {
    // Load vehicles from service
    this.vehicleData = [
      { id: 1, name: 'Truck #001' },
      { id: 2, name: 'Truck #002' },
      { id: 3, name: 'Van #001' }
    ];
  }
}
