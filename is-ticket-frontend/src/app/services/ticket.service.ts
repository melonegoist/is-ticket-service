import { Injectable, signal } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Ticket, TicketType, Color, Country, VenueType, Person, Venue} from '../models/ticket.model';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = 'http://localhost:7861/api/tickets';
  private tickets = signal<Ticket[]>([]);

  constructor(public http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 0:
          errorMessage = 'Unable to connect to server. Please check your internet connection.';
          break;
        case 400:
          errorMessage = error.error?.message || 'Bad request. Please check your input.';
          break;
        case 401:
          errorMessage = 'Your session has expired. Please login again.';

          localStorage.removeItem('token');
          localStorage.removeItem('userData');
          window.location.href = '/login';
          break;
        case 403:
          errorMessage = 'You do not have permission to perform this action.';
          break;
        case 404:
          errorMessage = error.error?.message || 'The requested resource was not found.';
          break;
        case 409:
          errorMessage = error.error?.message || 'Conflict: This resource already exists.';
          break;
        case 422:
          errorMessage = error.error?.message || 'Validation error. Please check your input.';
          break;
        case 500:
          errorMessage = 'Server error. Please try again later.';
          break;
        default:
          errorMessage = error.error?.message || error.message || `Server returned ${error.status}`;
      }
    }

    console.error('API Error:', error);
    return throwError(() => new Error(errorMessage));
  }

  getAllTickets(page: number = 0, size: number = 10, sort?: string, substring?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (sort) {
      // params = params.set('sort', sort);
      params = params.set('sort', sort);
    }
    if (substring) {
      params = params.set('substring', substring);
    }

    return this.http.get<any>(this.apiUrl, { params });
  }

  getTicketById(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.apiUrl}/${id}`);
  }

  createTicket(ticket: Partial<Ticket>): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/create-ticket`, ticket);
  }

  updateTicket(id: number, ticket: Partial<Ticket>): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.apiUrl}/${id}`, ticket)
      .pipe(catchError(this.handleError));
  }

  deleteTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  findByNameContains(substring: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/name-contains`, {
      params: { substring }
    });
  }

  findByNameStartsWith(prefix: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/name-starts-with`, {
      params: { prefix }
    });
  }

  findByNumberLessThan(number: number): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/number-less-than`, {
      params: { number: number.toString() }
    });
  }

  findByNumberGreaterThan(number: number): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/number-greater-than`, {
      params: { number: number.toString() }
    });
  }

  sellTicket(ticketId: number, salePrice: number, personId: any): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${ticketId}/sell`, { salePrice, personId });
  }

  createDiscountedTicket(originalTicketId: number, discount: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${originalTicketId}/clone-with-discount`, {
      discount
    });
  }

  getTicketsNumberSum(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/search/get-tickets-number-sum`, {})
  }

  getEnums(): Observable<any> {
    return this.http.get(`${this.apiUrl}/enums`);
  }

  updateTickets(tickets: Ticket[]): void {
    this.tickets.set(tickets);
  }

  getTickets() {
    return this.tickets.asReadonly();
  }

  // Add to TicketService class
  getAllPersons(): Observable<Person[]> {
    return this.http.get<Person[]>('http://localhost:7861/api/persons')
      .pipe(catchError(this.handleError));
  }

  createPerson(person: Person): Observable<Person> {
    return this.http.post<Person>('http://localhost:7861/api/persons', person)
      .pipe(catchError(this.handleError));
  }

  getAllVenues(): Observable<Venue[]> {
    return this.http.get<Venue[]>('http://localhost:7861/api/venues')
      .pipe(catchError(this.handleError));
  }

  createVenue(venue: Venue): Observable<Venue> {
    return this.http.post<Venue>('http://localhost:7861/api/venues', venue)
      .pipe(catchError(this.handleError));
  }
}
