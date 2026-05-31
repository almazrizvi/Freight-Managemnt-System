import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CustomersListComponent } from './customers-list.component';
import { CustomerDetailComponent } from './customer-detail.component';
import { CustomerCreateComponent } from './customer-create.component';

const routes: Routes = [
  {
    path: '',
    component: CustomersListComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: 'create',
    component: CustomerCreateComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: ':id',
    component: CustomerDetailComponent,
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
export class CustomersRoutingModule {}
