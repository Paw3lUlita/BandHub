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

@Injectable({ providedIn: 'root' })
export class ConcertService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/concerts';

  // Pobieranie listy
  getAll(): Observable<Concert[]> {
    return this.http.get<Concert[]>(this.apiUrl);
  }

  // Tworzenie koncertu (z pulami biletów)
  create(concert: CreateConcertRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, concert);
  }
}
