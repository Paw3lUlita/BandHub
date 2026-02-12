import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Modele wewnątrz serwisu - zgodnie z Twoim wzorcem
export interface Tour {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
}

export interface CreateTourRequest {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
}

export interface TourCost {
  id: string;
  title: string;
  amount: number;
  currency: string;
  date: string;
}

export interface CreateCostRequest {
  title: string;
  amount: number;
  currency: string;
  date: string;
}

export interface TourDetails extends Tour {
  description: string;
  costs: TourCost[];
}

@Injectable({ providedIn: 'root' })
export class LogisticsService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/logistics';

  getAllTours(): Observable<Tour[]> {
    return this.http.get<Tour[]>(`${this.apiUrl}/tours`);
  }

  createTour(request: CreateTourRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tours`, request);
  }

  getOneTour(id: string): Observable<TourDetails> {
    return this.http.get<TourDetails>(`${this.apiUrl}/tours/${id}`);
  }

  addCost(tourId: string, request: CreateCostRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tours/${tourId}/costs`, request);
  }
}
