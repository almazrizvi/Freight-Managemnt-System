import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';

type JobStatus = 'Running' | 'Paused' | 'Stopped';

interface MonitoredJob {
  id: string;
  job: string;
  module: string;
  status: JobStatus;
  processed: number;
  errors: number;
  lastUpdated: Date;
}

@Component({
  selector: 'app-user-activity',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule, MatSnackBarModule, MatTableModule],
  templateUrl: './user-activity.component.html',
  styleUrls: ['./user-activity.component.scss']
})
export class UserActivityComponent implements OnInit {
  displayedColumns: string[] = ['job', 'module', 'status', 'processed', 'errors', 'actions'];
  activities: MonitoredJob[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadActivity();
  }

  loadActivity(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.activities = [
        {
          id: 'shipment-sync',
          job: 'Shipment Status Sync',
          module: 'Shipments',
          status: 'Running',
          processed: 1248,
          errors: 1,
          lastUpdated: new Date()
        },
        {
          id: 'billing-close',
          job: 'Billing Close Batch',
          module: 'Billing',
          status: 'Paused',
          processed: 224,
          errors: 0,
          lastUpdated: new Date()
        },
        {
          id: 'user-audit',
          job: 'User Audit Export',
          module: 'Users',
          status: 'Stopped',
          processed: 0,
          errors: 0,
          lastUpdated: new Date()
        }
      ];
      this.isLoading = false;
    }, 300);
  }

  exportActivity(): void {
    const payload = JSON.stringify(this.activities, null, 2);
    const blob = new Blob([payload], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = 'operations-monitor.json';
    anchor.click();
    URL.revokeObjectURL(url);
  }

  startJob(jobId: string): void {
    this.updateStatus(jobId, 'Running');
  }

  pauseJob(jobId: string): void {
    this.updateStatus(jobId, 'Paused');
  }

  stopJob(jobId: string): void {
    this.updateStatus(jobId, 'Stopped');
  }

  private updateStatus(jobId: string, status: JobStatus): void {
    this.activities = this.activities.map((activity) =>
      activity.id === jobId ? { ...activity, status, lastUpdated: new Date() } : activity
    );
  }

  goBack(): void {
    this.router.navigate(['/admin/users']);
  }
}
