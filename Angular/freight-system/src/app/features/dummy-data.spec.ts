import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';

import { DummyData } from './dummy-data';

describe('DummyData', () => {
  let component: DummyData;
  let fixture: ComponentFixture<DummyData>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DummyData],
      providers: [provideZonelessChangeDetection()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DummyData);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
