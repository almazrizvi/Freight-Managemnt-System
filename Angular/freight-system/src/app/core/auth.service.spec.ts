import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideZonelessChangeDetection(),
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('should normalize missing RBAC fields to full access on login', () => {
    let result: string[] | undefined;

    service.login({ email: 'ops@freight.test', password: 'secret' }).subscribe((session) => {
      result = session.menuIds;
      expect(session.email).toBe('ops@freight.test');
      expect(session.authorities.length).toBeGreaterThan(0);
      expect(session.menuIds.length).toBeGreaterThan(0);
    });

    const request = httpTestingController.expectOne('http://localhost:9010/api/users/login');
    request.flush({
      token: 'jwt-token',
      userId: '42',
      email: 'ops@freight.test',
      fullName: 'Ops User',
      userType: 'INTERNAL',
      expiresIn: 3600,
      tokenType: 'Bearer',
      roles: [],
      authorities: [],
      menuIds: []
    });

    expect(result).toBeDefined();
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should clear the current user on logout', () => {
    service['currentUserSubject'].next({
      token: 'jwt-token',
      userId: '42',
      email: 'ops@freight.test',
      fullName: 'Ops User',
      userType: 'INTERNAL',
      expiresIn: 3600,
      tokenType: 'Bearer',
      roles: ['ADMIN'],
      authorities: ['shipments:view'],
      menuIds: ['shipments']
    });

    service.logout();

    expect(service.getCurrentUser()).toBeNull();
  });
});
