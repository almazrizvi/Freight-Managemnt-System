import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LoginService } from './login.service';
import { AuthService } from '../../core/auth.service';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username = 'demo';
  password = 'password';
  error = '';
  registerMode = false;
  regUsername = '';
  regPassword = '';
  regConfirm = '';
  regError = '';
  regSuccess = '';

  constructor(
    private router: Router,
    private loginService: LoginService,
    private authService: AuthService
  ) {}

  async login(): Promise<void> {
    this.error = '';
    console.log('Attempting login for', this.username);
    const ok = await this.loginService.authenticate(this.username, this.password);
    if (ok) {
      // Store user in auth service
      this.authService.login(this.username);
      this.router.navigate(['/']);
    } else {
      this.error = 'Invalid credentials';
    }
  }

  toggleRegister(): void {
    this.registerMode = !this.registerMode;
    this.regError = '';
    this.regSuccess = '';
  }

  async register(): Promise<void> {
    this.regError = '';
    this.regSuccess = '';
    if (!this.regUsername || !this.regPassword) {
      this.regError = 'Please provide username and password.';
      return;
    }
    if (this.regPassword !== this.regConfirm) {
      this.regError = 'Passwords do not match.';
      return;
    }
    const ok = await this.loginService.createUser(this.regUsername, this.regPassword);
    if (ok) {
      this.regSuccess = 'Account created — you can now sign in.';
      // Auto-login after registration
      this.authService.login(this.regUsername);
      setTimeout(() => {
        this.router.navigate(['/']);
      }, 1000);
    } else {
      this.regError = 'Could not create account (username may already exist).';
    }
  }
}
