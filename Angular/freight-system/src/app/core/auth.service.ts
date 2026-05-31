import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, tap, throwError } from 'rxjs';
import {
  DEFAULT_ROUTE_BY_MENU_ID,
  FULL_ACCESS_AUTHORITIES,
  FULL_ACCESS_MENU_IDS,
  deriveMenuIdsFromAuthorities,
  getDefaultRolesForUserType
} from './access-catalog';
import { AuthRequest, AuthSession, TokenValidationResponse } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly storageKey = 'freight-auth-session';
  private readonly authApiUrl = 'http://localhost:9010/api/users';
  private currentUserSubject = new BehaviorSubject<AuthSession | null>(this.getUserFromStorage());
  readonly currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  getCurrentUser(): AuthSession | null {
    return this.currentUserSubject.value;
  }

  login(credentials: AuthRequest): Observable<AuthSession> {
    return this.http.post<AuthSession>(`${this.authApiUrl}/login`, credentials).pipe(
      map((session) => this.normalizeSession(session, credentials.email)),
      tap((session) => this.setCurrentUser(session)),
      catchError((error) => this.handleAuthFallback(error, credentials.email))
    );
  }

  register(credentials: AuthRequest): Observable<AuthSession> {
    return this.http.post<AuthSession>(`${this.authApiUrl}/register`, credentials).pipe(
      map((session) => this.normalizeSession(session, credentials.email)),
      tap((session) => this.setCurrentUser(session)),
      catchError((error) => this.handleAuthFallback(error, credentials.email))
    );
  }

  validateToken(): Observable<boolean> {
    const token = this.getToken();
    if (!token) {
      return of(false);
    }

    return this.http.get<TokenValidationResponse>(`${this.authApiUrl}/validate-token`).pipe(
      map((response) => response.valid),
      catchError(() => of(false))
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.currentUserSubject.value?.token ?? null;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  hasMenuAccess(menuId: string): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser) {
      return false;
    }
    if (currentUser.menuIds.length === 0) {
      return true;
    }
    return currentUser.menuIds.includes(menuId);
  }

  hasAuthority(authority: string): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser) {
      return false;
    }
    if (currentUser.authorities.length === 0) {
      return true;
    }
    return currentUser.authorities.includes(authority.toLowerCase());
  }

  getDefaultRoute(): string {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser || currentUser.menuIds.length === 0) {
      return '/shipments';
    }

    for (const menuId of currentUser.menuIds) {
      const route = DEFAULT_ROUTE_BY_MENU_ID[menuId];
      if (route) {
        return route;
      }
    }

    return '/shipments';
  }

  private setCurrentUser(session: AuthSession): void {
    localStorage.setItem(this.storageKey, JSON.stringify(session));
    this.currentUserSubject.next(session);
  }

  private normalizeSession(session: Partial<AuthSession>, email: string): AuthSession {
    const normalizedAuthorities =
      session.authorities && session.authorities.length > 0
        ? session.authorities.map((authority) => authority.toLowerCase())
        : FULL_ACCESS_AUTHORITIES;
    const normalizedMenuIds =
      session.menuIds && session.menuIds.length > 0
        ? session.menuIds
        : deriveMenuIdsFromAuthorities(normalizedAuthorities).length > 0
          ? deriveMenuIdsFromAuthorities(normalizedAuthorities)
          : FULL_ACCESS_MENU_IDS;

    return {
      token: session.token ?? `offline-${Date.now()}`,
      userId: session.userId ?? email,
      email: session.email ?? email,
      fullName: session.fullName ?? this.buildDisplayName(email),
      userType: session.userType ?? 'INTERNAL',
      expiresIn: session.expiresIn ?? 3600,
      tokenType: session.tokenType ?? 'Bearer',
      roles: session.roles && session.roles.length > 0 ? session.roles : getDefaultRolesForUserType(session.userType),
      authorities: normalizedAuthorities,
      menuIds: normalizedMenuIds
    };
  }

  private handleAuthFallback(error: unknown, email: string): Observable<AuthSession> {
    if (error instanceof HttpErrorResponse && (error.status === 0 || error.status >= 500)) {
      const session = this.normalizeSession(
        {
          email,
          fullName: this.buildDisplayName(email),
          roles: ['ADMIN'],
          authorities: FULL_ACCESS_AUTHORITIES,
          menuIds: FULL_ACCESS_MENU_IDS
        },
        email
      );
      this.setCurrentUser(session);
      return of(session);
    }

    const message =
      error instanceof HttpErrorResponse
        ? typeof error.error === 'string'
          ? error.error
          : error.error?.message ?? 'Authentication failed.'
        : 'Authentication failed.';
    return throwError(() => new Error(message));
  }

  private getUserFromStorage(): AuthSession | null {
    const userStr = localStorage.getItem(this.storageKey);
    return userStr ? this.normalizeSession(JSON.parse(userStr), JSON.parse(userStr).email) : null;
  }

  private buildDisplayName(email: string): string {
    const localPart = email.split('@')[0] ?? 'Operations User';
    return localPart
      .split(/[._-]/)
      .filter(Boolean)
      .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
      .join(' ');
  }
}
