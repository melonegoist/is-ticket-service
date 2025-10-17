import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface UserRegistration {
  id: number;
  username: string;
  requestedRole: 'ROLE_USER' | 'ROLE_ADMIN';
  registrationDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

interface SystemStats {
  totalTickets: number;
  totalUsers: number;
  pendingRegistrations: number;
  activeAdmins: number;
}

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {
  // User registrations
  pendingRegistrations = signal<UserRegistration[]>([]);
  allRegistrations = signal<UserRegistration[]>([]);

  // System statistics
  systemStats = signal<SystemStats>({
    totalTickets: 0,
    totalUsers: 0,
    pendingRegistrations: 0,
    activeAdmins: 0
  });

  // UI state
  activeTab = signal<'registrations' | 'stats' | 'users'>('registrations');
  isLoading = signal(false);
  error = signal('');

  // Mock data for demonstration
  private mockRegistrations: UserRegistration[] = [
    {
      id: 1,
      username: 'new_admin',
      requestedRole: 'ROLE_ADMIN',
      registrationDate: '2024-01-15T10:30:00',
      status: 'PENDING'
    },
    {
      id: 2,
      username: 'regular_user',
      requestedRole: 'ROLE_USER',
      registrationDate: '2024-01-14T14:20:00',
      status: 'PENDING'
    },
    {
      id: 3,
      username: 'another_admin',
      requestedRole: 'ROLE_ADMIN',
      registrationDate: '2024-01-13T09:15:00',
      status: 'APPROVED'
    },
    {
      id: 4,
      username: 'rejected_user',
      requestedRole: 'ROLE_ADMIN',
      registrationDate: '2024-01-12T16:45:00',
      status: 'REJECTED'
    }
  ];

  private mockStats: SystemStats = {
    totalTickets: 156,
    totalUsers: 42,
    pendingRegistrations: 2,
    activeAdmins: 3
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);

    // Simulate API call delay
    setTimeout(() => {
      this.pendingRegistrations.set(
        this.mockRegistrations.filter(reg => reg.status === 'PENDING')
      );
      this.allRegistrations.set(this.mockRegistrations);
      this.systemStats.set(this.mockStats);
      this.isLoading.set(false);
    }, 1000);
  }

  approveRegistration(registration: UserRegistration): void {
    this.isLoading.set(true);

    // Simulate API call
    setTimeout(() => {
      const updatedRegistrations = this.allRegistrations().map(reg =>
        reg.id === registration.id
          ? { ...reg, status: 'APPROVED' as const }
          : reg
      );

      this.allRegistrations.set(updatedRegistrations);
      this.pendingRegistrations.set(
        updatedRegistrations.filter(reg => reg.status === 'PENDING')
      );
      this.updateStats();
      this.isLoading.set(false);
    }, 500);
  }

  rejectRegistration(registration: UserRegistration): void {
    this.isLoading.set(true);

    // Simulate API call
    setTimeout(() => {
      const updatedRegistrations = this.allRegistrations().map(reg =>
        reg.id === registration.id
          ? { ...reg, status: 'REJECTED' as const }
          : reg
      );

      this.allRegistrations.set(updatedRegistrations);
      this.pendingRegistrations.set(
        updatedRegistrations.filter(reg => reg.status === 'PENDING')
      );
      this.updateStats();
      this.isLoading.set(false);
    }, 500);
  }

  private updateStats(): void {
    this.systemStats.update(stats => ({
      ...stats,
      pendingRegistrations: this.pendingRegistrations().length
    }));
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'status-pending';
      case 'APPROVED': return 'status-approved';
      case 'REJECTED': return 'status-rejected';
      default: return 'status-unknown';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'PENDING': return 'Pending';
      case 'APPROVED': return 'Approved';
      case 'REJECTED': return 'Rejected';
      default: return 'Unknown';
    }
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user';
  }

  getRoleText(role: string): string {
    return role === 'ROLE_ADMIN' ? 'Administrator' : 'User';
  }

  setActiveTab(tab: 'registrations' | 'stats' | 'users'): void {
    this.activeTab.set(tab);
  }

  getCurrentUser() {
    return this.authService.getCurrentUser()();
  }

  navigateToTickets(): void {
    this.router.navigate(['/tickets']);
  }

  // Format date for display
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
