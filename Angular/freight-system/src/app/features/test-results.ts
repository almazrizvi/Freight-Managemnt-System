import { Component, OnInit } from '@angular/core';
import { UserService } from '../core/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-test-results',
  template: `
    <table>
      <tr *ngFor="let item of items">
        <td>{{ item }}</td>
      </tr>
    </table>
  `,
  imports: [CommonModule]
})
export class TestResultsComponent implements OnInit {
  items: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getTestResults().subscribe((items: any[]) => {
      this.items = items;
    });
  }
}