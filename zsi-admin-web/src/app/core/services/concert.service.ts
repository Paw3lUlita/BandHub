import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketPoolRequest {
  name: string;
  price: number;
  currency: string;
  totalQuantity: number;
}

export interface CreateConcertRequest {
  name: string;
  date: string; // ISO String z formularza
  description: string;
  imageUrl: string;
  venueId: string;
  ticketPools: TicketPoolRequest[];
}

export interface Concert {
  id: string;
  name: string;
  date: string;
  venueName: string;
  city: string;
}

export interface TicketPoolResponse {
  id: string;
  name: string;
  price: number;
  currency: string;
  totalQuantity: number;
  remainingQuantity: number;
}

export interface ConcertDetails {
  id: string;
  name: string;
  date: string;
  description: string;
  imageUrl: string;
  venueName: string;
  venueCity: string;
  ticketPools: TicketPoolResponse[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  sortBy: string;
  sortDir: string;
  query: string;
}

export interface TicketPoolSalesRow {
  poolId: string;
  poolName: string;
  sold: number;
  remaining: number;
  total: number;
  revenue: number;
  currency: string;
}

export interface ConcertTicketingSummary {
  concertId: string;
  concertName: string;
  venueCapacity: number;
  totalSold: number;
  totalRevenue: number;
  currency: string;
  pools: TicketPoolSalesRow[];
}

export interface AttendeeRow {
  ticketCode: string;
  userId: string;
  orderId: string | null;
  poolName: string;
  purchaseDate: string | null;
}

@Injectable({ providedIn: 'root' })
export class ConcertService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/concerts';

  getAll(): Observable<Concert[]> {
    return this.http.get<Concert[]>(this.apiUrl);
  }

  getPage(params: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
    q?: string;
  }): Observable<PageResponse<Concert>> {
    const queryParams = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'date',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });

    return this.http.get<PageResponse<Concert>>(`${this.apiUrl}/page?${queryParams.toString()}`);
  }

  create(concert: CreateConcertRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, concert);
  }

  getOne(id: string): Observable<ConcertDetails> {
    return this.http.get<ConcertDetails>(`${this.apiUrl}/${id}`);
  }

  getTicketingSummary(concertId: string): Observable<ConcertTicketingSummary> {
    return this.http.get<ConcertTicketingSummary>(`${this.apiUrl}/${concertId}/ticketing/summary`);
  }

  getTicketingAttendees(concertId: string): Observable<AttendeeRow[]> {
    return this.http.get<AttendeeRow[]>(`${this.apiUrl}/${concertId}/ticketing/attendees`);
  }

  downloadAttendeesCsv(concertId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${concertId}/ticketing/attendees/export`, {
      responseType: 'blob'
    });
  }
}
