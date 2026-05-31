import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VehiclesListComponent } from './vehicles-list.component';
import { VehicleDetailComponent } from './vehicle-detail.component';
import { VehicleCreateComponent } from './vehicle-create.component';

const routes: Routes = [
  {
    path: '',
    component: VehiclesListComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: 'create',
    component: VehicleCreateComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    }
  },
  {
    path: ':id',
    component: VehicleDetailComponent,
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
export class VehiclesRoutingModule {}
