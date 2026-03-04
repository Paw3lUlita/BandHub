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

export interface UpdateTourRequest {
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

export interface TourRevenue {
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

export interface UpdateCostRequest {
  title: string;
  amount: number;
  currency: string;
  date: string;
}

export interface CreateRevenueRequest {
  title: string;
  amount: number;
  currency: string;
  date: string;
}

export interface UpdateRevenueRequest {
  title: string;
  amount: number;
  currency: string;
  date: string;
}

export interface TourDetails extends Tour {
  description: string;
  costs: TourCost[];
  revenues: TourRevenue[];
}

export interface TourProfitability {
  totalCosts: number;
  ticketRevenue: number;
  manualRevenue: number;
  totalRevenue: number;
  balance: number;
  currency: string;
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

  updateTour(id: string, request: UpdateTourRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/tours/${id}`, request);
  }

  deleteTour(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tours/${id}`);
  }

  getOneTour(id: string): Observable<TourDetails> {
    return this.http.get<TourDetails>(`${this.apiUrl}/tours/${id}`);
  }

  addCost(tourId: string, request: CreateCostRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tours/${tourId}/costs`, request);
  }

  updateCost(tourId: string, costId: string, request: UpdateCostRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/tours/${tourId}/costs/${costId}`, request);
  }

  deleteCost(tourId: string, costId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tours/${tourId}/costs/${costId}`);
  }

  addRevenue(tourId: string, request: CreateRevenueRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tours/${tourId}/revenues`, request);
  }

  updateRevenue(tourId: string, revenueId: string, request: UpdateRevenueRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/tours/${tourId}/revenues/${revenueId}`, request);
  }

  deleteRevenue(tourId: string, revenueId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tours/${tourId}/revenues/${revenueId}`);
  }

  getProfitability(id: string): Observable<TourProfitability> {
    return this.http.get<TourProfitability>(`${this.apiUrl}/tours/${id}/profitability`);
  }
}
