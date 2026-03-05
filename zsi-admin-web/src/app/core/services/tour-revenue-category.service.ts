import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TourRevenueCategory {
  id: string;
  code: string;
  name: string;
  active: boolean;
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

export interface CreateTourCategoryRequest {
  code: string;
  name: string;
  active: boolean;
}

@Injectable({ providedIn: 'root' })
export class TourRevenueCategoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/tour-revenue-categories';

  getAll(): Observable<TourRevenueCategory[]> {
    return this.http.get<TourRevenueCategory[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TourRevenueCategory>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'name',
      sortDir: params.sortDir ?? 'asc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TourRevenueCategory>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TourRevenueCategory> {
    return this.http.get<TourRevenueCategory>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTourCategoryRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTourCategoryRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
