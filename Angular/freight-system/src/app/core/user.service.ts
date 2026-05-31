import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  // Backend API endpoints through API Gateway
  private apiBaseUrl = 'http://localhost:9010/api';
  private userServiceUrl = `${this.apiBaseUrl}/users`;

  private httpHeaders = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private http: HttpClient) { }

  /**
   * Create a new user
   */
  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.userServiceUrl, user, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get all active users
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.userServiceUrl, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get user by ID
   */
  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.userServiceUrl}/${id}`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Update user
   */
  updateUser(id: string, user: User): Observable<User> {
    return this.http.put<User>(`${this.userServiceUrl}/${id}`, user, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Delete user (soft delete)
   */
  deleteUser(id: string, deletedBy: string): Observable<void> {
    return this.http.delete<void>(`${this.userServiceUrl}/${id}`, { 
      params: { deletedBy },
      headers: this.httpHeaders 
    });
  }

  /**
   * Toggle user status (active/inactive)
   */
  toggleUserStatus(id: string, isActive: boolean): Observable<User> {
    return this.http.put<User>(`${this.userServiceUrl}/${id}/status`, {}, {
      params: { isActive: isActive.toString() },
      headers: this.httpHeaders
    });
  }

  /**
   * Search users by name or email
   */
  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.userServiceUrl}/search`, {
      params: { query },
      headers: this.httpHeaders
    });
  }

  /**
   * Get users by type
   */
  getUsersByType(userType: 'INTERNAL' | 'CUSTOMER' | 'DRIVER'): Observable<User[]> {
    return this.http.get<User[]>(`${this.userServiceUrl}/type/${userType}`, {
      headers: this.httpHeaders
    });
  }

  /**
   * Delete multiple users (soft delete)
   */
  bulkDeleteUsers(userIds: string[], deletedBy: string): Observable<void> {
    return this.http.post<void>(`${this.userServiceUrl}/bulk-delete`, userIds, {
      params: { deletedBy },
      headers: this.httpHeaders
    });
  }

  /**
   * Get user count
   */
  getUserCount(): Observable<number> {
    return this.http.get<number>(`${this.userServiceUrl}/count`, {
      headers: this.httpHeaders
    });
  }

  /**
   * Get AWBs (Air Way Bills)
   */
  getAWBs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/awbs`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get customers
   */
  getCustomers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/customers`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get shipments
   */
  getShipments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/shipments`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get vehicles
   */
  getVehicles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/vehicles`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get reports
   */
  getReports(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/reports`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get main content
   */
  getMainContent(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/main-content`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get test results
   */
  getTestResults(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/test-results`, { 
      headers: this.httpHeaders 
    });
  }

  /**
   * Get errors
   */
  getErrors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBaseUrl}/errors`, { 
      headers: this.httpHeaders 
    });
  }
}