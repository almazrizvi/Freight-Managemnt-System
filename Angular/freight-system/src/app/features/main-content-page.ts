import { Component, OnInit } from '@angular/core';
import { UserService } from '../core/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-content-page',
  template: `
    <table>
      <tr *ngFor="let item of items">
        <td>{{ item }}</td>
      </tr>
    </table>
  `,
  imports: [CommonModule]
})
export class MainContentPageComponent implements OnInit {
  items: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getMainContent().subscribe((items: any[]) => {
      this.items = items;
    });
  }
}