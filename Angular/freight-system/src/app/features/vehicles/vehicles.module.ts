import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VehiclesRoutingModule } from './vehicles-routing.module';
import { VehiclesListComponent } from './vehicles-list.component';
import { VehicleDetailComponent } from './vehicle-detail.component';
import { VehicleCreateComponent } from './vehicle-create.component';

@NgModule({
  imports: [
    CommonModule,
    VehiclesRoutingModule,
    VehiclesListComponent,
    VehicleDetailComponent,
    VehicleCreateComponent
  ]
})
export class VehiclesModule {}
