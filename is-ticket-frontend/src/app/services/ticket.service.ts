import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket, TicketType, Color, Country, VenueType } from '../models/ticket.model';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = 'http://localhost:8080/api/tickets';
  private tickets = signal<Ticket[]>([]);

  constructor(private http: HttpClient) {}

  getAllTickets(page: number = 0, size: number = 10, sort?: string, filter?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (sort) {
      // params = params.set('sort', sort);
      params = params.set('sort', 'id');
    }
    if (filter) {
      params = params.set('filter', filter);
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
    return this.http.put<Ticket>(`${this.apiUrl}/${id}`, ticket);
  }

  deleteTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  findByNameContaining(substring: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/name-containing`, {
      params: { substring }
    });
  }

  findByNameStartingWith(prefix: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/name-starting-with`, {
      params: { prefix }
    });
  }

  findByNumberLessThan(number: number): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/search/number-less-than`, {
      params: { number: number.toString() }
    });
  }

  sellTicket(ticketId: number, price: number, person: any): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${ticketId}/sell`, { price, person });
  }

  createDiscountedTicket(originalTicketId: number, discountPercent: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${originalTicketId}/discount`, {
      discountPercent
    });
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
}
