import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReportsListComponent } from './reports-list.component';
import { ReportDetailComponent } from './report-detail.component';
import { ReportCreateComponent } from './report-create.component';

const routes: Routes = [
  {
    path: '',
    component: ReportsListComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: 'create',
    component: ReportCreateComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: ':id',
    component: ReportDetailComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportsRoutingModule {}
