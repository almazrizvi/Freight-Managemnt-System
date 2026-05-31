import { Component, OnInit } from '@angular/core';
import { UserService } from '../core/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-awb-numbers-table',
  template: `
    <table>
      <tr *ngFor="let awb of awbs">
        <td>{{ awb }}</td>
      </tr>
    </table>
  `,
  imports: [CommonModule]
})
export class AwbNumbersTableComponent implements OnInit {
  awbs: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getAWBs().subscribe((awbs: any[]) => {
      this.awbs = awbs;
    });
  }
}