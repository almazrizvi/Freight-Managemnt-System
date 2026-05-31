import { of, throwError } from 'rxjs';
import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { RbacAdminService } from './rbac-admin.service';
import { UserService } from './user.service';

describe('RbacAdminService', () => {
  let service: RbacAdminService;
  let httpTestingController: HttpTestingController;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  beforeEach(() => {
    localStorage.clear();
    userServiceSpy = jasmine.createSpyObj<UserService>('UserService', ['getAllUsers', 'getUserById']);

    TestBed.configureTestingModule({
      providers: [
        RbacAdminService,
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: UserService, useValue: userServiceSpy }
      ]
    });

    service = TestBed.inject(RbacAdminService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('should derive authorities for admin role', () => {
    const authorities = service.getAuthoritiesForRoles(['ADMIN']);

    expect(authorities).toContain('shipments:view');
    expect(authorities).toContain('admin_users:create');
  });

  it('should fall back to user data when the access endpoint is unavailable', () => {
    userServiceSpy.getAllUsers.and.returnValue(
      of([
        {
          id: '42',
          email: 'ops@freight.test',
          fullName: 'Ops User',
          userType: 'INTERNAL',
          isActive: true
        }
      ] as any)
    );

    let usersLength = 0;
    service.getUserAccessList().subscribe((users) => {
      usersLength = users.length;
      expect(users[0].roleCodes?.length).toBeGreaterThan(0);
    });

    const request = httpTestingController.expectOne('http://localhost:9010/api/users/access/users');
    request.flush('offline', { status: 500, statusText: 'Server Error' });

    expect(usersLength).toBe(1);
  });
});
