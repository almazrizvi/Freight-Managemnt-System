import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { UsersListComponent } from '../features/users-list/users-list.component';
import { UserFormComponent } from '../features/user-form/user-form.component';
import { UserRolesComponent } from '../features/user-roles/user-roles.component';
import { UserActivityComponent } from '../features/user-activity/user-activity.component';
import { UserPermissionsComponent } from '../features/user-permissions/user-permissions.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    data: {
      showHeader: true,
      showSidebar: true,
      showFooter: true
    },
    children: [
      {
        path: 'shipments',
        data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'shipments' },
        loadChildren: () => import('../features/shipments/shipments.module').then(m => m.ShipmentsModule)
      },
      {
        path: 'vehicles',
        data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'vehicles' },
        loadChildren: () => import('../features/vehicles/vehicles.module').then(m => m.VehiclesModule)
      },
      {
        path: 'customers',
        data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'customers' },
        loadChildren: () => import('../features/customers/customers.module').then(m => m.CustomersModule)
      },
      {
        path: 'reports',
        data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'reports' },
        loadChildren: () => import('../features/reports/reports.module').then(m => m.ReportsModule)
      },
      {
        path: 'admin',
        data: { showHeader: true, showSidebar: true, showFooter: true },
        children: [
          {
            path: 'users',
            component: UsersListComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_users' }
          },
          {
            path: 'users/create',
            component: UserFormComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_users_create' }
          },
          {
            path: 'users/:id/edit',
            component: UserFormComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_users_create' }
          },
          {
            path: 'users/roles',
            component: UserRolesComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_roles' }
          },
          {
            path: 'users/activity',
            component: UserActivityComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_activity' }
          },
          {
            path: 'users/permissions',
            component: UserPermissionsComponent,
            data: { showHeader: true, showSidebar: true, showFooter: true, menuId: 'admin_permissions' }
          }
        ]
      },
      { path: '', redirectTo: 'shipments', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LayoutRoutingModule {}
