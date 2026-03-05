import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TourSettlement {
  id: string;
  tourId: string;
  settledBy: string | null;
  settledAt: string | null;
  totalCosts: number | null;
  totalRevenue: number | null;
  balance: number | null;
  currency: string | null;
  notes: string | null;
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

export interface CreateTourSettlementRequest {
  tourId: string;
  settledBy: string | null;
  settledAt: string | null;
  totalCosts: number | null;
  totalRevenue: number | null;
  balance: number | null;
  currency: string | null;
  notes: string | null;
}

@Injectable({ providedIn: 'root' })
export class TourSettlementService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/tour-settlements';

  getAll(): Observable<TourSettlement[]> {
    return this.http.get<TourSettlement[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TourSettlement>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'settledAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TourSettlement>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TourSettlement> {
    return this.http.get<TourSettlement>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTourSettlementRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTourSettlementRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
