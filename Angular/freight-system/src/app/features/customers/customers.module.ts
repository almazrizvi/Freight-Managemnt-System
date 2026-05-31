import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomersRoutingModule } from './customers-routing.module';
import { CustomersListComponent } from './customers-list.component';
import { CustomerDetailComponent } from './customer-detail.component';
import { CustomerCreateComponent } from './customer-create.component';

@NgModule({
  imports: [
    CommonModule,
    CustomersRoutingModule,
    CustomersListComponent,
    CustomerDetailComponent,
    CustomerCreateComponent
  ]
})
export class CustomersModule {}
