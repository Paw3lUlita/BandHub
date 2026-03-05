import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FanDevice {
  id: string;
  fanId: string;
  deviceToken: string;
  platform: string;
  appVersion: string | null;
  lastSeenAt: string | null;
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

export interface CreateFanDeviceRequest {
  fanId: string;
  deviceToken: string;
  platform: string;
  appVersion: string | null;
  lastSeenAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class FanDeviceService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/fan-devices';

  getAll(): Observable<FanDevice[]> {
    return this.http.get<FanDevice[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<FanDevice>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<FanDevice>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<FanDevice> {
    return this.http.get<FanDevice>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateFanDeviceRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateFanDeviceRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
