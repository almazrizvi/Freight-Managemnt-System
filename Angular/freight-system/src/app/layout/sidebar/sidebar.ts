import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MenuService, MenuItem } from '../../shared/services/menu.service';
import { AuthService } from '../../core/auth.service';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  standalone: true,
  selector: 'app-sidebar',
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss'],
  animations: [
    trigger('expandCollapse', [
      state('open', style({
        height: '*',
        opacity: 1,
        visibility: 'visible'
      })),
      state('closed', style({
        height: '0px',
        opacity: 0,
        visibility: 'hidden'
      })),
      transition('open <=> closed', [
        animate('300ms ease-in-out')
      ])
    ])
  ]
})
export class Sidebar implements OnInit {
  menuItems: MenuItem[] = [];
  private allMenuItems: MenuItem[] = [];
  private openSubmenus: Set<string> = new Set();

  constructor(
    private menuService: MenuService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.menuService.getMenuItems().subscribe(items => {
      this.allMenuItems = this.normalizeMenuItems(items);
      this.applyAccessFilter();
    });

    this.authService.currentUser$.subscribe(() => this.applyAccessFilter());
  }

  /**
   * Normalize menu items to ensure both 'label' and 'title' properties are set
   */
  private normalizeMenuItems(items: MenuItem[]): MenuItem[] {
    return items.map(item => ({
      ...item,
      label: item.label || item.title,
      route: item.route || item.angularRoute,
      children: item.children ? this.normalizeMenuItems(item.children) : undefined
    }));
  }

  private applyAccessFilter(): void {
    this.menuItems = this.filterAccessibleMenus(this.allMenuItems);
  }

  private filterAccessibleMenus(items: MenuItem[]): MenuItem[] {
    return items
      .map((item) => ({
        ...item,
        children: item.children ? this.filterAccessibleMenus(item.children) : undefined
      }))
      .filter((item) => {
        const hasDirectAccess = !item.menuId || this.authService.hasMenuAccess(item.menuId);
        const hasAccessibleChildren = !!item.children && item.children.length > 0;
        return hasDirectAccess || hasAccessibleChildren;
      });
  }

  /**
   * Toggle submenu visibility
   */
  toggleSubmenu(item: MenuItem): void {
    const key = this.getSubmenuKey(item);
    if (key && item.children && item.children.length > 0) {
      if (this.openSubmenus.has(key)) {
        this.openSubmenus.delete(key);
      } else {
        this.openSubmenus.add(key);
      }
    }
  }

  /**
   * Check if submenu is open
   */
  isSubmenuOpen(item: MenuItem): boolean {
    const key = this.getSubmenuKey(item);
    return key ? this.openSubmenus.has(key) : false;
  }

  private getSubmenuKey(item: MenuItem): string | null {
    return item.label || item.title || item.menuId || item.route || null;
  }
}
