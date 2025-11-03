import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { Ticket, Coordinates, Person, Venue, TicketType, Color, Country, VenueType, Address, Location } from '../../models/ticket.model';

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
    personId: '',
    venueId: 0,
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
      // address: { street: '' }
    }
  });

  // Person selection
  existingPersons = signal<Person[]>([]);
  selectedPersonId = signal<string | null>(null);
  personMode = signal<'select' | 'create'>('select');

  // Venue selection
  existingVenues = signal<Venue[]>([]);
  selectedVenueId = signal<number | null>(null);
  venueMode = signal<'select' | 'create'>('select');

  // Enums for selects
  ticketTypes = Object.values(TicketType);
  colors = Object.values(Color);
  countries = Object.values(Country);
  venueTypes = Object.values(VenueType);

  isEdit = signal(false);
  isLoading = signal(false);
  isCreatingPerson = signal(false);
  isCreatingVenue = signal(false);
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
    } else {
      this.loadExistingPersons();
      this.loadExistingVenues();
    }
  }

  loadTicket(id: number): void {
    this.isLoading.set(true);
    this.ticketService.getTicketById(id).subscribe({
      next: (ticket) => {
        this.ticketData.set(ticket);
        this.loadExistingPersons();
        this.loadExistingVenues();
        this.isLoading.set(false);
      },
      error: (error) => {
        this.error.set('Failed to load ticket');
        this.isLoading.set(false);
      }
    });
  }

  loadExistingPersons(): void {
    this.ticketService.getAllPersons().subscribe({
      next: (persons) => {
        this.existingPersons.set(persons);
      },
      error: (error) => {
        console.error('Failed to load persons:', error);
      }
    });
  }

  loadExistingVenues(): void {
    this.ticketService.getAllVenues().subscribe({
      next: (venues: Venue[]) => {
        this.existingVenues.set(venues);
      },
      error: (error: any) => {
        console.error('Failed to load venues:', error);
      }
    });
  }

  // Person methods
  onPersonModeChange(mode: 'select' | 'create'): void {
    this.personMode.set(mode);
    this.error.set('');
  }

  onPersonSelect(personId: string): void {
    this.selectedPersonId.set(personId);
    const selectedPerson = this.existingPersons().find(p => p.passportID === personId);
    if (selectedPerson) {
      this.ticketData.update(current => ({
        ...current,
        person: selectedPerson
      }));
    }
  }

  createNewPerson(): void {
    const personData = this.ticketData().person;
    if (!personData || !this.validatePersonForm()) {
      return;
    }

    this.isCreatingPerson.set(true);
    this.ticketService.createPerson(personData).subscribe({
      next: (createdPerson) => {
        // Update the form with created person
        this.ticketData.update(current => ({
          ...current,
          person: createdPerson
        }));

        // Reload persons list to include the new one
        this.loadExistingPersons();

        // Switch back to select mode and select the new person
        this.personMode.set('select');
        this.selectedPersonId.set(createdPerson.passportID);
        this.isCreatingPerson.set(false);
      },
      error: (error) => {
        this.error.set(error.error?.message || 'Failed to create person');
        this.isCreatingPerson.set(false);
      }
    });
  }

  validatePersonForm(): boolean {
    const person = this.ticketData().person;

    if (!person?.passportID?.trim()) {
      this.error.set('Passport ID is required');
      return false;
    }

    if (person.passportID.length > 43) {
      this.error.set('Passport ID must not exceed 43 characters');
      return false;
    }

    if (!person?.eyeColor) {
      this.error.set('Eye color is required');
      return false;
    }

    if (!person?.nationality) {
      this.error.set('Nationality is required');
      return false;
    }

    this.error.set('');
    return true;
  }

  // Venue methods
  onVenueModeChange(mode: 'select' | 'create'): void {
    this.venueMode.set(mode);
    this.error.set('');
  }

  onVenueSelect(venueId: number): void {
    this.selectedVenueId.set(venueId);
    const selectedVenue = this.existingVenues().find(v => v.id === venueId);
    if (selectedVenue) {
      this.ticketData.update(current => ({
        ...current,
        venue: selectedVenue
      }));
    }
  }

  createNewVenue(): void {
    const venueData = this.ticketData().venue;
    if (!venueData || !this.validateVenueForm()) {
      return;
    }

    this.isCreatingVenue.set(true);
    this.ticketService.createVenue(venueData).subscribe({
      next: (createdVenue) => {
        // Update the form with created venue (полный объект Venue)
        this.ticketData.update(current => ({
          ...current,
          venue: createdVenue
        }));

        // Reload venues list to include the new one
        this.loadExistingVenues();

        // Switch back to select mode and select the new venue
        this.venueMode.set('select');
        this.selectedVenueId.set(createdVenue.id);
        this.isCreatingVenue.set(false);
      },
      error: (error) => {
        this.error.set(error.error?.message || 'Failed to create venue');
        this.isCreatingVenue.set(false);
      }
    });
  }

  validateVenueForm(): boolean {
    const venue = this.ticketData().venue;

    if (!venue?.name?.trim()) {
      this.error.set('Venue name is required');
      return false;
    }

    if (!venue?.capacity || venue.capacity <= 0) {
      this.error.set('Venue capacity must be greater than 0');
      return false;
    }

    if (!venue?.type) {
      this.error.set('Venue type is required');
      return false;
    }

    this.error.set('');
    return true;
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

  updateLocation<K extends keyof Location>(field: K, value: Location[K] | null): void {
    this.ticketData.update(current => {
      const currentPerson = current.person!;
      const currentLocation = currentPerson.location || { x: undefined, y: undefined, z: undefined };

      return {
        ...current,
        person: {
          ...currentPerson,
          location: {
            ...currentLocation,
            [field]: value
          }
        }
      } as Partial<Ticket>;
    });
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
          // ...current.venue!.address,
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

    console.log(finalTicket);

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

  getPersonDisplayName(person: Person): string {
    let displayName = `${person.passportID} (${person.eyeColor} eyes, ${person.nationality}`;

    if (person.hairColor) {
      displayName += `, ${person.hairColor} hair`;
    }

    if (person.location) {
      displayName += `, Location: ${person.location.x}, ${person.location.y}, ${person.location.z}`;
    }

    displayName += ')';
    return displayName;
  }

  getVenueDisplayName(venue: Venue): string {
    return `${venue.name} (${venue.type}, capacity: ${venue.capacity})`;
  }

  getFormattedLocation(): string {
    const location = this.ticketData().person?.location;
    if (!location) {
      return 'Not specified';
    }

    const coordinates = [];

    if (location.x !== undefined && location.x !== null) {
      coordinates.push(`X: ${location.x}`);
    }
    if (location.y !== undefined && location.y !== null) {
      coordinates.push(`Y: ${location.y}`);
    }
    if (location.z !== undefined && location.z !== null) {
      coordinates.push(`Z: ${location.z}`);
    }

    return coordinates.length > 0 ? coordinates.join(', ') : 'Not specified';
  }
}
