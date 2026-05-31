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
  selector: 'app-customers-list',
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
      <h2>Customers</h2>
      <span class="spacer"></span>
      <button mat-raised-button color="accent" [routerLink]="['/customers', 'create']">
        <mat-icon>add</mat-icon>
        New Customer
      </button>
    </mat-toolbar>

    <div class="container">
      <mat-card>
        <mat-card-content>
          <p>Customers list component. Coming soon...</p>
          <table mat-table [dataSource]="customerData" class="customers-table">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let element">{{ element.id }}</td>
            </ng-container>

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Customer Name</th>
              <td mat-cell *matCellDef="let element">{{ element.name }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let element">
                <button mat-icon-button [routerLink]="['/customers', element.id]" title="View">
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
    .customers-table {
      width: 100%;
    }
  `]
})
export class CustomersListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'actions'];
  customerData: any[] = [];

  ngOnInit(): void {
    // Load customers from service
    this.customerData = [
      { id: 1, name: 'Acme Corp' },
      { id: 2, name: 'Global Logistics' },
      { id: 3, name: 'Express Delivery' }
    ];
  }
}
