import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FanFavorite {
  id: string;
  fanId: string;
  favoriteType: string;
  referenceId: string;
  createdAt: string;
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

export interface CreateFanFavoriteRequest {
  fanId: string;
  favoriteType: string;
  referenceId: string;
}

@Injectable({ providedIn: 'root' })
export class FanFavoriteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/fan-favorites';

  getAll(): Observable<FanFavorite[]> {
    return this.http.get<FanFavorite[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<FanFavorite>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<FanFavorite>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<FanFavorite> {
    return this.http.get<FanFavorite>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateFanFavoriteRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateFanFavoriteRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
