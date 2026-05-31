import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsRoutingModule } from './reports-routing.module';
import { ReportsListComponent } from './reports-list.component';
import { ReportDetailComponent } from './report-detail.component';
import { ReportCreateComponent } from './report-create.component';

@NgModule({
  imports: [
    CommonModule,
    ReportsRoutingModule,
    ReportsListComponent,
    ReportDetailComponent,
    ReportCreateComponent
  ]
})
export class ReportsModule {}
