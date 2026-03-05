import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SetlistItem {
  id: string;
  setlistId: string;
  songTitle: string;
  songOrder: number;
  durationSeconds: number | null;
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

export interface CreateSetlistItemRequest {
  setlistId: string;
  songTitle: string;
  songOrder: number;
  durationSeconds: number | null;
}

@Injectable({ providedIn: 'root' })
export class SetlistItemService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/setlist-items';

  getAll(): Observable<SetlistItem[]> {
    return this.http.get<SetlistItem[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<SetlistItem>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'songOrder',
      sortDir: params.sortDir ?? 'asc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<SetlistItem>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<SetlistItem> {
    return this.http.get<SetlistItem>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateSetlistItemRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateSetlistItemRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
