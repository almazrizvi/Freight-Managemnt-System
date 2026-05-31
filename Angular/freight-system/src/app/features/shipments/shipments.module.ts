import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShipmentsRoutingModule } from './shipments-routing.module';
import { ShipmentsListComponent } from './shipments-list/shipments-list.component';
import { ShipmentDetailComponent } from './shipment-detail/shipment-detail.component';
import { ShipmentCreateComponent } from './shipment-create/shipment-create.component';

@NgModule({
  imports: [
    CommonModule,
    ShipmentsRoutingModule,
    ShipmentsListComponent,
    ShipmentDetailComponent,
    ShipmentCreateComponent
  ]
})
export class ShipmentsModule {}
