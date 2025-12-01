import { Component, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Ticket } from '../../models/ticket.model';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.scss']
})
export class TicketListComponent implements OnInit, OnDestroy {
  tickets = signal<Ticket[]>([]);
  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  totalElements = signal(0);
  isLoading = signal(false);
  searchTerm = signal('');
  sortField = signal('id');
  sortDirection = signal('asc');importing = signal(false);
  importError = signal('');
  importResult = signal<Ticket[] | null>(null);
  selectedFile = signal<File | null>(null);

  private subscription?: Subscription;

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.loadTickets();
    // Subscribe to real-time updates would go here
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  loadTickets(): void {
    this.isLoading.set(true);

    this.ticketService.getAllTickets(
      this.currentPage(),
      this.pageSize(),
      `${this.sortField()},${this.sortDirection()}`,
      this.searchTerm() || undefined
    ).subscribe({
      next: (response) => {
        this.tickets.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading tickets:', error);
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadTickets();
  }

  onSearch(): void {
    this.currentPage.set(0);
    this.loadTickets();
  }

  onSort(field: string): void {
    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }
    this.loadTickets();
  }

  viewTicket(ticket: Ticket): void {
    this.router.navigate(['/tickets', ticket.id]);
  }

  editTicket(ticket: Ticket): void {
    if (this.canEdit(ticket)) {
      this.router.navigate(['/tickets', ticket.id, 'edit']);
    }
  }

  deleteTicket(ticket: Ticket): void {
    if (this.canDelete(ticket) && confirm('Are you sure you want to delete this ticket?')) {
      this.ticketService.deleteTicket(ticket.id).subscribe({
        next: () => {
          this.loadTickets();
        },
        error: (error) => {
          alert('Cannot delete ticket: ' + (error.error?.message || 'Ticket is referenced by other objects'));
        }
      });
    }
  }

  canEdit(ticket: Ticket): boolean {
    const currentUser = this.authService.getCurrentUser()();
    return this.authService.isAdmin() || ticket.createdBy === currentUser?.username;
  }

  canDelete(ticket: Ticket): boolean {
    return this.canEdit(ticket);
  }

  getCurrentUser() {
    return this.authService.getCurrentUser()();
  }

  logout(): void {
    console.log(this.getCurrentUser())
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getSortIcon(field: string): string {
    if (this.sortField() !== field) return '↕️';
    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const total = this.totalPages();
    const current = this.currentPage();

    // Show max 5 pages around current page
    let start = Math.max(0, current - 2);
    let end = Math.min(total - 1, current + 2);

    // Adjust if we're near the start
    if (current < 3) {
      end = Math.min(total - 1, 4);
    }

    // Adjust if we're near the end
    if (current > total - 4) {
      start = Math.max(0, total - 5);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.item(0) ?? null;
    this.selectedFile.set(file);
    this.importError.set('');
    this.importResult.set(null);
  }

  importTickets(): void {
    if (!this.selectedFile()) {
      this.importError.set('Please select an XML file to import.');
      return;
    }

    this.importing.set(true);
    this.importError.set('');

    this.ticketService.importTicketsFromXml(this.selectedFile()!).subscribe({
      next: (tickets) => {
        this.importResult.set(tickets);
        this.importing.set(false);
        this.loadTickets();
      },
      error: (error) => {
        this.importError.set(error.message || 'Failed to import tickets.');
        this.importing.set(false);
      }
    });
  }

}
