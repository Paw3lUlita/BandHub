import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Modele (Interfejsy) wewnątrz serwisu - Twój styl
export interface Venue {
  id: string;
  name: string;
  city: string;
  street: string;
  capacity: number;
  contactEmail: string;
}

export interface CreateVenueRequest {
  name: string;
  city: string;
  street: string;
  capacity: number;
  contactEmail: string;
}

@Injectable({ providedIn: 'root' })
export class VenueService {
  private http = inject(HttpClient);
  // URL do backendu
  private apiUrl = 'http://localhost:8080/api/admin/venues';

  // A. Lista Miejsc (GET)
  getAll(): Observable<Venue[]> {
    return this.http.get<Venue[]>(this.apiUrl);
  }

  // B. Dodawanie Miejsca (POST)
  create(venue: CreateVenueRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, venue);
  }

  // C. Usuwanie Miejsca (DELETE)
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
