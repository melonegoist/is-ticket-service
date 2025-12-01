import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';

export interface User {
  username: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN';
}

export interface AuthResponse { // todo: check
  token: string;
  username: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser = signal<User | null>(null);
  private apiUrl = 'http://localhost:8080/auth';
  private tokenExpirationTimer: any;

  constructor(private http: HttpClient) {}

  login(login: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/sign-in`, { login, password })
      .pipe(
        tap(response => {
          this.handleAuthentication(
            response.username,
            response.role as 'ROLE_USER' || 'ROLE_ADMIN',
            response.token,
            100000000 // todo
          );
        })
      );
  }

  register(username: string, password: string, isAdmin: boolean = false, email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/sign-up`, { username, password, email, isAdmin });
  }

  setCurrentUser(user: User): void {
    this.currentUser.set(user);
    localStorage.setItem('userData', JSON.stringify(user));
  }

  getCurrentUser() {
    return this.currentUser.asReadonly();
  }

  autoLogin(): void {
    const userData = localStorage.getItem('userData');
    const token = localStorage.getItem('token');
    const expiresIn = localStorage.getItem('expiresIn');

    if (!userData || !token || !expiresIn) {
      return;
    }

    const parsedUserData: User = JSON.parse(userData);
    const expiration = new Date(expiresIn);
    const now = new Date();

    if (expiration <= now) {
      this.logout();
      return;
    }

    this.currentUser.set(parsedUserData);

    const remaining = expiration.getTime() - now.getTime();
    this.autoLogout(remaining);
  }

  autoLogout(expirationTime: number): void {
    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
      alert("Logged out");
    }, expirationTime);
  }

  logout(): void {
    this.currentUser.set(null);
    localStorage.removeItem('userData');
    localStorage.removeItem('token');
    localStorage.removeItem('expiresIn');

    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }

  private handleAuthentication(
    username: string,
    role: 'ROLE_USER' | 'ROLE_ADMIN',
    token: string,
    expiresIn?: number
  ): void {
    const user: User = { username, role };

    const expirationDuration = expiresIn ? expiresIn * 1000 : 60 * 60 * 1000;
    const expirationDate = new Date(new Date().getTime() + expirationDuration);

    localStorage.setItem('userData', JSON.stringify(user));
    localStorage.setItem('token', token);
    localStorage.setItem('expiresIn', expirationDate.toString());

    this.currentUser.set(user);
    this.autoLogout(expirationDuration);
  }

  initializeUser(): void {
    this.autoLogin();
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ROLE_ADMIN';
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }
}
