import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TourLeg {
  id: string;
  tourId: string;
  concertId: string | null;
  legOrder: number;
  city: string;
  venueName: string | null;
  legDate: string | null;
  plannedBudget: number | null;
  currency: string | null;
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

export interface CreateTourLegRequest {
  tourId: string;
  concertId: string | null;
  legOrder: number;
  city: string;
  venueName: string | null;
  legDate: string | null;
  plannedBudget: number | null;
  currency: string | null;
}

@Injectable({ providedIn: 'root' })
export class TourLegService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/tour-legs';

  getAll(): Observable<TourLeg[]> {
    return this.http.get<TourLeg[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TourLeg>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'legOrder',
      sortDir: params.sortDir ?? 'asc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TourLeg>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TourLeg> {
    return this.http.get<TourLeg>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTourLegRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTourLegRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
