import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderStatusHistory {
  id: string;
  orderId: string;
  oldStatus: string | null;
  newStatus: string;
  changedBy: string | null;
  changedAt: string;
  note: string | null;
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

export interface CreateOrderStatusHistoryRequest {
  orderId: string;
  oldStatus: string | null;
  newStatus: string;
  changedBy: string | null;
  note: string | null;
}

export interface UpdateOrderStatusHistoryRequest {
  oldStatus: string | null;
  newStatus: string;
  changedBy: string | null;
  note: string | null;
}

@Injectable({ providedIn: 'root' })
export class OrderStatusHistoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/order-status-history';

  getAll(): Observable<OrderStatusHistory[]> {
    return this.http.get<OrderStatusHistory[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<OrderStatusHistory>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'changedAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<OrderStatusHistory>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<OrderStatusHistory> {
    return this.http.get<OrderStatusHistory>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateOrderStatusHistoryRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: UpdateOrderStatusHistoryRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
