import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { redirectTo: state.url } });
      return false;
    }

    const requiredMenuId = route.data?.['menuId'] as string | undefined;
    if (requiredMenuId && !this.authService.hasMenuAccess(requiredMenuId)) {
      this.router.navigateByUrl(this.authService.getDefaultRoute());
      return false;
    }

    const requiredAuthority = route.data?.['requiredAuthority'] as string | undefined;
    if (requiredAuthority && !this.authService.hasAuthority(requiredAuthority)) {
      this.router.navigateByUrl(this.authService.getDefaultRoute());
      return false;
    }

    return true;
  }
}
