import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  username = signal('');
  email = signal('');
  password = signal('');
  confirmPassword = signal('');
  isAdmin = signal(false);
  isLoading = signal(false);
  error = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  onRegister(): void {
    if (!this.username() || !this.password()) {
      this.error.set('Please fill in all fields');
      return;
    }

    if (this.password() !== this.confirmPassword()) {
      this.error.set('Passwords do not match');
      return;
    }

    if (this.password().length < 6) {
      this.error.set('Password must be at least 6 characters long');
      return;
    }

    this.isLoading.set(true);
    this.error.set('');

    this.authService.register(this.username(), this.password(), this.isAdmin(), this.email()).subscribe({
      next: () => {
        this.router.navigate(['/login'], {
          queryParams: { registered: true }
        });
      },
      error: (error) => {
        this.error.set(error.error?.message || 'Registration failed');
        this.isLoading.set(false);
      }
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
