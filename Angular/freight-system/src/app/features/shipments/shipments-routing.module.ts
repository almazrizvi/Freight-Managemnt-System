import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ShipmentsListComponent } from './shipments-list/shipments-list.component';
import { ShipmentDetailComponent } from './shipment-detail/shipment-detail.component';
import { ShipmentCreateComponent } from './shipment-create/shipment-create.component';

const routes: Routes = [
  {
    path: '',
    component: ShipmentsListComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: 'create',
    component: ShipmentCreateComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: ':id',
    component: ShipmentDetailComponent,
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
export class ShipmentsRoutingModule {}

