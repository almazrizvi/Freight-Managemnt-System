import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { firstValueFrom } from 'rxjs';
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
  email = 'admin@freight.local';
  password = 'password';
  error = '';
  isSubmitting = false;
  registerMode = false;
  regEmail = '';
  regPassword = '';
  regConfirm = '';
  regError = '';
  regSuccess = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  async login(): Promise<void> {
    this.error = '';
    this.isSubmitting = true;
    try {
      await firstValueFrom(
        this.authService.login({
          email: this.email.trim(),
          password: this.password
        })
      );

      const redirectTo = this.route.snapshot.queryParamMap.get('redirectTo');
      this.router.navigateByUrl(redirectTo || this.authService.getDefaultRoute());
    } catch (error) {
      this.error = error instanceof Error ? error.message : 'Invalid credentials';
    } finally {
      this.isSubmitting = false;
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
    this.isSubmitting = true;
    if (!this.regEmail || !this.regPassword) {
      this.regError = 'Please provide email and password.';
      this.isSubmitting = false;
      return;
    }
    if (this.regPassword !== this.regConfirm) {
      this.regError = 'Passwords do not match.';
      this.isSubmitting = false;
      return;
    }
    try {
      await firstValueFrom(
        this.authService.register({
          email: this.regEmail.trim(),
          password: this.regPassword
        })
      );
      this.regSuccess = 'Account created — you can now sign in.';
      setTimeout(() => {
        this.router.navigateByUrl(this.authService.getDefaultRoute());
      }, 1000);
    } catch (error) {
      this.regError = error instanceof Error ? error.message : 'Could not create account.';
    } finally {
      this.isSubmitting = false;
    }
  }
}
