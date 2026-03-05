import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FanNotificationRead {
  id: string;
  notificationId: string;
  fanId: string;
  readAt: string;
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

export interface CreateFanNotificationReadRequest {
  notificationId: string;
  fanId: string;
  readAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class FanNotificationReadService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/fan-notification-reads';

  getAll(): Observable<FanNotificationRead[]> {
    return this.http.get<FanNotificationRead[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<FanNotificationRead>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'readAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<FanNotificationRead>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<FanNotificationRead> {
    return this.http.get<FanNotificationRead>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateFanNotificationReadRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateFanNotificationReadRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
