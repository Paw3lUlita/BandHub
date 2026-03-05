import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Setlist {
  id: string;
  concertId: string;
  concertName: string;
  title: string;
  createdBy: string | null;
  publishedAt: string | null;
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

export interface CreateSetlistRequest {
  concertId: string;
  title: string;
  createdBy: string | null;
  publishedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class SetlistService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/setlists';

  getAll(): Observable<Setlist[]> {
    return this.http.get<Setlist[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<Setlist>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<Setlist>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<Setlist> {
    return this.http.get<Setlist>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateSetlistRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateSetlistRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
