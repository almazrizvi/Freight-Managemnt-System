import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { LoginComponent } from '../login.component';
import { AuthService } from '../../../core/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            login: () =>
              of({
                token: 'token',
                userId: '1',
                email: 'ops@freight.test',
                fullName: 'Ops User',
                userType: 'INTERNAL',
                expiresIn: 3600,
                tokenType: 'Bearer',
                roles: ['ADMIN'],
                authorities: ['shipments:view'],
                menuIds: ['shipments']
              }),
            register: () =>
              of({
                token: 'token',
                userId: '1',
                email: 'ops@freight.test',
                fullName: 'Ops User',
                userType: 'INTERNAL',
                expiresIn: 3600,
                tokenType: 'Bearer',
                roles: ['ADMIN'],
                authorities: ['shipments:view'],
                menuIds: ['shipments']
              }),
            getDefaultRoute: () => '/shipments'
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
