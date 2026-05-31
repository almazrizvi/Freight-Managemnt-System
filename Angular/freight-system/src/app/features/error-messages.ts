import { Component, OnInit } from '@angular/core';
import { UserService } from '../core/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-messages',
  template: `
    <table>
      <tr *ngFor="let item of items">
        <td>{{ item }}</td>
      </tr>
    </table>
  `,
  imports: [CommonModule]
})
export class ErrorMessagesComponent implements OnInit {
  items: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getErrors().subscribe((items: any[]) => {
      this.items = items;
    });
  }
}