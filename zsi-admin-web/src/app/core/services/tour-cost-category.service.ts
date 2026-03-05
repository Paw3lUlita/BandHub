import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TourCostCategory {
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
export class TourCostCategoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/tour-cost-categories';

  getAll(): Observable<TourCostCategory[]> {
    return this.http.get<TourCostCategory[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TourCostCategory>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'name',
      sortDir: params.sortDir ?? 'asc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TourCostCategory>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TourCostCategory> {
    return this.http.get<TourCostCategory>(`${this.apiUrl}/${id}`);
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
