import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { UserPermissionsComponent } from './user-permissions.component';
import { RbacAdminService } from '../../core/rbac-admin.service';

describe('UserPermissionsComponent', () => {
  let component: UserPermissionsComponent;
  let fixture: ComponentFixture<UserPermissionsComponent>;
  let rbacAdminServiceSpy: jasmine.SpyObj<RbacAdminService>;

  beforeEach(async () => {
    rbacAdminServiceSpy = jasmine.createSpyObj<RbacAdminService>('RbacAdminService', [
      'getRoles',
      'getRolePermissionMatrix',
      'saveRolePermissionMatrix'
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
    rbacAdminServiceSpy.getRolePermissionMatrix.and.returnValue(
      of({
        roleCode: 'ADMIN',
        roleName: 'Administrator',
        description: 'Full access',
        permissions: [
          {
            menuId: 'shipments',
            title: 'Shipments',
            description: 'Manage shipments',
            route: '/shipments',
            icon: 'local_shipping',
            availableActions: ['VIEW', 'CREATE', 'UPDATE', 'DELETE', 'APPROVE'],
            assignedActions: ['VIEW']
          }
        ]
      })
    );

    await TestBed.configureTestingModule({
      imports: [UserPermissionsComponent],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap({ role: 'ADMIN' })
            }
          }
        },
        { provide: RbacAdminService, useValue: rbacAdminServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserPermissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load the permission matrix for the selected role', () => {
    expect(component.permissions.length).toBe(1);
    expect(component.permissions[0].assignedActions).toContain('VIEW');
  });
});
