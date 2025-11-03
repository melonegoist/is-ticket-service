import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = signal('');
  password = signal('');
  isLoading = signal(false);
  error = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    if (!this.username() || !this.password()) {
      this.error.set('Please fill in all fields');
      return;
    }

    this.isLoading.set(true);
    this.error.set('');

    this.authService.login(this.username(), this.password()).subscribe({
      next: (response) => {
        this.authService.setCurrentUser({
          username: this.username(),
          role: response.role as any
        });
        localStorage.setItem('token', response.token);
        this.router.navigate(['/tickets']);
      },
      error: (error) => {
        this.error.set('Invalid username or password');
        this.isLoading.set(false);
      }
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
