import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketOrder {
  id: string;
  userId: string;
  concertId: string;
  status: string;
  totalAmount: number;
  currency: string;
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

export interface CreateTicketOrderRequest {
  id: string;
  userId: string;
  concertId: string;
  status: string;
  totalAmount: number;
  currency: string;
}

@Injectable({ providedIn: 'root' })
export class TicketOrderService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticket-orders';

  getAll(): Observable<TicketOrder[]> {
    return this.http.get<TicketOrder[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TicketOrder>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TicketOrder>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TicketOrder> {
    return this.http.get<TicketOrder>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTicketOrderRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTicketOrderRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
