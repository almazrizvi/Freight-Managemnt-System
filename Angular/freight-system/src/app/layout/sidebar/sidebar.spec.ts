import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

import { Sidebar } from './sidebar';
import { AuthService } from '../../core/auth.service';
import { MenuService } from '../../shared/services/menu.service';

describe('Sidebar', () => {
  let component: Sidebar;
  let fixture: ComponentFixture<Sidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sidebar],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        {
          provide: MenuService,
          useValue: {
            getMenuItems: () => of([])
          }
        },
        {
          provide: AuthService,
          useValue: {
            currentUser$: of(null),
            hasMenuAccess: () => true
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Sidebar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
