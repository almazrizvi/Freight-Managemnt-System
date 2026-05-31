import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dummy-data',
  standalone: true,
  imports: [CommonModule],
  template: `
    <p>Dummy data component</p>
  `,
})
export class DummyDataComponent {
}