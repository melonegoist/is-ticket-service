import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { Ticket, Person, Color, Country } from '../../models/ticket.model';

@Component({
  selector: 'app-special-operations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './special-operations.component.html',
  styleUrls: ['./special-operations.component.scss']
})
export class SpecialOperationsComponent implements OnInit {
  // Operation 1: Search by name containing
  searchSubstring = signal('');
  searchResults1 = signal<Ticket[]>([]);
  isLoading1 = signal(false);

  // Operation 2: Search by name starting with
  searchPrefix = signal('');
  searchResults2 = signal<Ticket[]>([]);
  isLoading2 = signal(false);

  // Operation 3: Search by number less than
  searchNumber = signal<number | null>(null);
  searchResults3 = signal<Ticket[]>([]);
  isLoading3 = signal(false);

  // Operation 4: Sell ticket
  selectedTicketId = signal<number | null>(null);
  sellPrice = signal<number | null>(null);
  sellPerson = signal<Partial<Person>>({
    eyeColor: Color.GREEN,
    hairColor: undefined,
    passportID: '',
    nationality: Country.SPAIN
  });
  isSelling = signal(false);
  sellResult = signal<any>(null);

  // Operation 5: Create discounted ticket
  originalTicketId = signal<number | null>(null);
  discountPercent = signal<number | null>(null);
  isCreatingDiscounted = signal(false);
  discountedTicketResult = signal<Ticket | null>(null);

  // Available tickets for selection
  availableTickets = signal<Ticket[]>([]);
  colors = Object.values(Color);
  countries = Object.values(Country);

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAvailableTickets();
  }

  loadAvailableTickets(): void {
    this.ticketService.getAllTickets(0, 1000).subscribe({
      next: (response) => {
        this.availableTickets.set(response.content);
      },
      error: (error) => {
        console.error('Error loading tickets:', error);
      }
    });
  }

  // Operation 1: Search by name containing
  searchByNameContaining(): void {
    if (!this.searchSubstring().trim()) {
      return;
    }

    this.isLoading1.set(true);
    this.ticketService.findByNameContaining(this.searchSubstring()).subscribe({
      next: (tickets) => {
        this.searchResults1.set(tickets);
        this.isLoading1.set(false);
      },
      error: (error) => {
        console.error('Error searching tickets:', error);
        this.isLoading1.set(false);
      }
    });
  }

  // Operation 2: Search by name starting with
  searchByNameStartingWith(): void {
    if (!this.searchPrefix().trim()) {
      return;
    }

    this.isLoading2.set(true);
    this.ticketService.findByNameStartingWith(this.searchPrefix()).subscribe({
      next: (tickets) => {
        this.searchResults2.set(tickets);
        this.isLoading2.set(false);
      },
      error: (error) => {
        console.error('Error searching tickets:', error);
        this.isLoading2.set(false);
      }
    });
  }

  // Operation 3: Search by number less than
  searchByNumberLessThan(): void {
    if (!this.searchNumber()) {
      return;
    }

    this.isLoading3.set(true);
    this.ticketService.findByNumberLessThan(this.searchNumber()!).subscribe({
      next: (tickets) => {
        this.searchResults3.set(tickets);
        this.isLoading3.set(false);
      },
      error: (error) => {
        console.error('Error searching tickets:', error);
        this.isLoading3.set(false);
      }
    });
  }

  // Operation 4: Sell ticket
  sellTicket(): void {
    if (!this.selectedTicketId() || !this.sellPrice() || !this.sellPerson().passportID) {
      return;
    }

    this.isSelling.set(true);
    this.ticketService.sellTicket(
      this.selectedTicketId()!,
      this.sellPrice()!,
      this.sellPerson()
    ).subscribe({
      next: (result) => {
        this.sellResult.set(result);
        this.isSelling.set(false);
        this.loadAvailableTickets(); // Refresh available tickets
      },
      error: (error) => {
        console.error('Error selling ticket:', error);
        this.isSelling.set(false);
        this.sellResult.set({ error: error.error?.message || 'Failed to sell ticket' });
      }
    });
  }

  // Operation 5: Create discounted ticket
  createDiscountedTicket(): void {
    if (!this.originalTicketId() || !this.discountPercent()) {
      return;
    }

    this.isCreatingDiscounted.set(true);
    this.ticketService.createDiscountedTicket(
      this.originalTicketId()!,
      this.discountPercent()!
    ).subscribe({
      next: (ticket) => {
        this.discountedTicketResult.set(ticket);
        this.isCreatingDiscounted.set(false);
        this.loadAvailableTickets(); // Refresh available tickets
      },
      error: (error) => {
        console.error('Error creating discounted ticket:', error);
        this.isCreatingDiscounted.set(false);
      }
    });
  }

  // Helper methods
  getCurrentUser() {
    return this.authService.getCurrentUser()();
  }

  navigateToTickets(): void {
    this.router.navigate(['/tickets']);
  }

  updateSellPersonField<K extends keyof Person>(field: K, value: Person[K]): void {
    this.sellPerson.update(current => ({
      ...current,
      [field]: value
    }));
  }

  resetOperation(operationNumber: number): void {
    switch (operationNumber) {
      case 1:
        this.searchSubstring.set('');
        this.searchResults1.set([]);
        break;
      case 2:
        this.searchPrefix.set('');
        this.searchResults2.set([]);
        break;
      case 3:
        this.searchNumber.set(null);
        this.searchResults3.set([]);
        break;
      case 4:
        this.selectedTicketId.set(null);
        this.sellPrice.set(null);
        this.sellPerson.set({
          eyeColor: Color.GREEN,
          hairColor: undefined,
          passportID: '',
          nationality: Country.SPAIN
        });
        this.sellResult.set(null);
        break;
      case 5:
        this.originalTicketId.set(null);
        this.discountPercent.set(null);
        this.discountedTicketResult.set(null);
        break;
    }
  }
}
