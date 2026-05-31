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
    // TODO: Implement authentication logic later
    // For now, allow all access for development
    // Uncomment below when ready to implement authentication
    
    // const isLoggedIn = this.authService.isLoggedIn();
    // if (!isLoggedIn) {
    //   this.router.navigate(['/login']);
    //   return false;
    // }
    
    return true; // Allow access for now
  }
}
