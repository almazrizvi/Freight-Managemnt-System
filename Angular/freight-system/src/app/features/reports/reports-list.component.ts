import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-reports-list',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatPaginatorModule,
    MatSortModule,
    MatToolbarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    RouterModule
  ],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <h2>Reports</h2>
      <span class="spacer"></span>
      <button mat-raised-button color="accent" [routerLink]="['/reports', 'create']">
        <mat-icon>add</mat-icon>
        Generate Report
      </button>
    </mat-toolbar>

    <div class="container">
      <mat-card>
        <mat-card-content>
          <p>Reports component. Coming soon...</p>
          <table mat-table [dataSource]="reportData" class="reports-table">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let element">{{ element.id }}</td>
            </ng-container>

            <ng-container matColumnDef="title">
              <th mat-header-cell *matHeaderCellDef>Report Title</th>
              <td mat-cell *matCellDef="let element">{{ element.title }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let element">
                <button mat-icon-button [routerLink]="['/reports', element.id]" title="View">
                  <mat-icon>visibility</mat-icon>
                </button>
                <button mat-icon-button title="Download">
                  <mat-icon>download</mat-icon>
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
    .reports-table {
      width: 100%;
    }
  `]
})
export class ReportsListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'title', 'actions'];
  reportData: any[] = [];

  ngOnInit(): void {
    // Load reports from service
    this.reportData = [
      { id: 1, title: 'Monthly Shipment Report' },
      { id: 2, title: 'Quarterly Performance Report' },
      { id: 3, title: 'Annual Summary Report' }
    ];
  }
}
