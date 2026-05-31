import { Injectable } from '@angular/core';

interface User {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private users: User[] = [
    { username: 'demo', password: 'password' }
  ];

  async authenticate(username: string, password: string): Promise<boolean> {
    // Simulate async operation
    await new Promise(resolve => setTimeout(resolve, 300));
    
    const user = this.users.find(u => u.username === username);
    if (!user) {
      console.log('User not found:', username);
      return false;
    }
    
    const isValid = user.password === password;
    console.log('Authentication result for', username, ':', isValid);
    return isValid;
  }

  async createUser(username: string, password: string): Promise<boolean> {
    // Simulate async operation
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // Check if user already exists
    const exists = this.users.some(u => u.username === username);
    if (exists) {
      console.log('User already exists:', username);
      return false;
    }
    
    // Create new user
    this.users.push({ username, password });
    console.log('User created successfully:', username);
    return true;
  }
}
