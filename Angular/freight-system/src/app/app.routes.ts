import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';

export const routes: Routes = [
  // Login route (public - implement authentication later)
  { path: 'login', loadChildren: () => import('./features/login/login.module').then(m => m.LoginModule) },
  
  // Main layout with all features
  { path: '', canActivate: [AuthGuard], loadChildren: () => import('./layout/layout.module').then(m => m.LayoutModule) },
  
  // Catch-all redirect
  { path: '**', redirectTo: '' }
];

