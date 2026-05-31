import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface MenuItem {
  menuId?: string;
  title?: string;
  icon: string;
  angularRoute?: string;
  route?: string;
  parentId?: string;
  displayOrder?: number;
  isActive?: boolean;
  label?: string;
  children?: MenuItem[];
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiUrl = 'http://localhost:9090/api/menus';
  
  private defaultMenuItems: MenuItem[] = [
    {
      label: 'Shipments',
      icon: 'local_shipping',
      route: '/shipments'
    },
    {
      label: 'Vehicles',
      icon: 'directions_car',
      route: '/vehicles'
    },
    {
      label: 'Customers',
      icon: 'people',
      route: '/customers'
    },
    {
      label: 'Reports',
      icon: 'assessment',
      route: '/reports'
    },
    {
      label: 'Admin',
      icon: 'admin_panel_settings',
      children: [
        {
          label: 'User Management',
          icon: 'person',
          route: '/admin/users'
        },
        {
          label: 'Create User',
          icon: 'person_add',
          route: '/admin/users/create'
        },
        {
          label: 'User Roles',
          icon: 'security',
          route: '/admin/users/roles'
        },
        {
          label: 'User Activity',
          icon: 'history',
          route: '/admin/users/activity'
        },
        {
          label: 'User Permissions',
          icon: 'vpn_key',
          route: '/admin/users/permissions'
        }
      ]
    }
  ];

  constructor(private http: HttpClient) {}

  /**
   * Get menu items from backend API.
   * Falls back to default menu items if API is unavailable.
   */
  getMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(this.apiUrl).pipe(
      catchError(() => {
        console.warn('Failed to fetch menus from API, using default menus');
        return of(this.transformDefaultMenus(this.defaultMenuItems));
      })
    );
  }

  /**
   * Get all menus including inactive ones (admin use)
   */
  getAllMenusIncludeInactive(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiUrl}/all`).pipe(
      catchError(() => {
        console.warn('Failed to fetch all menus from API');
        return of([]);
      })
    );
  }

  /**
   * Get root level menus only
   */
  getRootMenus(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiUrl}/root`).pipe(
      catchError(() => {
        console.warn('Failed to fetch root menus from API');
        return of([]);
      })
    );
  }

  /**
   * Transform default menu items to match the API structure
   */
  private transformDefaultMenus(menus: MenuItem[]): MenuItem[] {
    return menus.map(menu => ({
      ...menu,
      icon: menu.icon,
      route: menu.route || menu.angularRoute,
      children: menu.children ? this.transformDefaultMenus(menu.children) : undefined
    }));
  }
}
