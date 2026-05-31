import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MenuService, MenuItem } from '../../shared/services/menu.service';
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
  private openSubmenus: Set<string> = new Set();

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.menuService.getMenuItems().subscribe(items => {
      // Normalize items to ensure label is set
      this.menuItems = this.normalizeMenuItems(items);
    });
  }

  /**
   * Normalize menu items to ensure both 'label' and 'title' properties are set
   */
  private normalizeMenuItems(items: MenuItem[]): MenuItem[] {
    return items.map(item => ({
      ...item,
      label: item.label || item.title,
      children: item.children ? this.normalizeMenuItems(item.children) : undefined
    }));
  }

  /**
   * Toggle submenu visibility
   */
  toggleSubmenu(item: MenuItem): void {
    if (item.children && item.children.length > 0) {
      const key = item.label || item.title;
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
    return this.openSubmenus.has(item.label || item.title);
  }
}
