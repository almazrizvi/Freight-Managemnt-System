import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DummyDataComponent } from './dummy-data.component';

@NgModule({
  imports: [CommonModule, DummyDataComponent],
  exports: [DummyDataComponent]
})
export class DummyDataModule {
}
