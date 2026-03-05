import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FanNotification {
  id: string;
  fanId: string;
  broadcast: boolean;
  title: string;
  message: string;
  module: string | null;
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

export interface CreateFanNotificationRequest {
  id: string;
  fanId: string | null;
  broadcast: boolean;
  title: string;
  message: string;
  module: string | null;
}

@Injectable({ providedIn: 'root' })
export class FanNotificationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/fan-notifications';

  getAll(): Observable<FanNotification[]> {
    return this.http.get<FanNotification[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<FanNotification>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<FanNotification>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<FanNotification> {
    return this.http.get<FanNotification>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateFanNotificationRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateFanNotificationRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
