import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { UserRolesComponent } from './user-roles.component';
import { RbacAdminService } from '../../core/rbac-admin.service';

describe('UserRolesComponent', () => {
  let component: UserRolesComponent;
  let fixture: ComponentFixture<UserRolesComponent>;
  let rbacAdminServiceSpy: jasmine.SpyObj<RbacAdminService>;

  beforeEach(async () => {
    rbacAdminServiceSpy = jasmine.createSpyObj<RbacAdminService>('RbacAdminService', [
      'getRoles',
      'getUserAccessList',
      'assignRoles',
      'getPermissionSummary'
    ]);

    rbacAdminServiceSpy.getRoles.and.returnValue(
      of([
        {
          roleCode: 'ADMIN',
          roleName: 'Administrator',
          description: 'Full access',
          systemRole: true,
          active: true
        }
      ])
    );
    rbacAdminServiceSpy.getUserAccessList.and.returnValue(
      of([
        {
          userId: '42',
          id: '42',
          email: 'ops@freight.test',
          fullName: 'Ops User',
          userType: 'INTERNAL',
          isActive: true,
          roleCodes: ['ADMIN'],
          authorities: ['shipments:view'],
          menuIds: ['shipments']
        } as any
      ])
    );
    rbacAdminServiceSpy.getPermissionSummary.and.returnValue({
      moduleCount: 1,
      authorityCount: 1,
      menuIds: ['shipments']
    });

    await TestBed.configureTestingModule({
      imports: [UserRolesComponent],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap({}) } }
        },
        { provide: RbacAdminService, useValue: rbacAdminServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserRolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load users with role assignments', () => {
    expect(component.users.length).toBe(1);
    expect(component.users[0].roleCodes).toEqual(['ADMIN']);
  });
});
