import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { Ticket, Coordinates, Person, Venue, TicketType, Color, Country, VenueType, Address } from '../../models/ticket.model';

@Component({
  selector: 'app-ticket-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-form.component.html',
  styleUrls: ['./ticket-form.component.scss']
})
export class TicketFormComponent implements OnInit {
  // Main form data
  ticketData = signal<Partial<Ticket>>({
    name: '',
    price: 0,
    discount: 0,
    coordinates: { x: 0, y: 0 },
    ticketType: undefined,
    person: {
      eyeColor: Color.GREEN,
      hairColor: undefined,
      location: undefined,
      passportID: '',
      nationality: Country.SPAIN
    },
    venue: {
      id: 0,
      name: '',
      capacity: 0,
      type: VenueType.CINEMA,
      address: { street: '' }
    }
  });

  // Enums for selects
  ticketTypes = Object.values(TicketType);
  colors = Object.values(Color);
  countries = Object.values(Country);
  venueTypes = Object.values(VenueType);

  isEdit = signal(false);
  isLoading = signal(false);
  error = signal('');

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const ticketId = this.route.snapshot.params['id'];
    if (ticketId) {
      this.isEdit.set(true);
      this.loadTicket(Number(ticketId));
    }
  }

  loadTicket(id: number): void {
    this.isLoading.set(true);
    this.ticketService.getTicketById(id).subscribe({
      next: (ticket) => {
        this.ticketData.set(ticket);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.error.set('Failed to load ticket');
        this.isLoading.set(false);
      }
    });
  }

  // Update methods for nested objects
  updateTicketField<K extends keyof Ticket>(field: K, value: Ticket[K]): void {
    this.ticketData.update(current => ({
      ...current,
      [field]: value
    }));
  }

  updateCoordinates<K extends keyof Coordinates>(field: K, value: Coordinates[K]): void {
    this.ticketData.update(current => ({
      ...current,
      coordinates: {
        ...current.coordinates!,
        [field]: value
      }
    }));
  }

  updatePerson<K extends keyof Person>(field: K, value: Person[K]): void {
    this.ticketData.update(current => ({
      ...current,
      person: {
        ...current.person!,
        [field]: value
      }
    }));
  }

  updateVenue<K extends keyof Venue>(field: K, value: Venue[K]): void {
    this.ticketData.update(current => ({
      ...current,
      venue: {
        ...current.venue!,
        [field]: value
      }
    }));
  }

  updateAddress(field: keyof Address, value: string): void {
    this.ticketData.update(current => ({
      ...current,
      venue: {
        ...current.venue!,
        address: {
          ...current.venue!.address,
          [field]: value
        }
      }
    }));
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading.set(true);

    const finalTicket = this.ticketData();

    const operation = this.isEdit()
      ? this.ticketService.updateTicket(this.ticketData().id!, finalTicket)
      : this.ticketService.createTicket(finalTicket);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/tickets']);
      },
      error: (error) => {
        this.error.set(error.error?.message || 'Operation failed');
        this.isLoading.set(false);
      }
    });
  }

  validateForm(): boolean {
    const ticket = this.ticketData();

    if (!ticket.name?.trim()) {
      this.error.set('Name is required');
      return false;
    }

    if (!ticket.price || ticket.price <= 0) {
      this.error.set('Price must be greater than 0');
      return false;
    }

    if (!ticket.discount || ticket.discount <= 0 || ticket.discount > 100) {
      this.error.set('Discount must be between 1 and 100');
      return false;
    }

    if (!ticket.coordinates?.y) {
      this.error.set('Y coordinate is required');
      return false;
    }

    if (!ticket.person?.passportID?.trim()) {
      this.error.set('Passport ID is required');
      return false;
    }

    if (ticket.person.passportID.length > 43) {
      this.error.set('Passport ID must not exceed 43 characters');
      return false;
    }

    if (!ticket.venue?.name?.trim()) {
      this.error.set('Venue name is required');
      return false;
    }

    if (!ticket.venue?.capacity || ticket.venue.capacity <= 0) {
      this.error.set('Venue capacity must be greater than 0');
      return false;
    }

    this.error.set('');
    return true;
  }

  getCurrentUser() {
    return this.authService.getCurrentUser()();
  }

  onCancel(): void {
    this.router.navigate(['/tickets']);
  }
}
