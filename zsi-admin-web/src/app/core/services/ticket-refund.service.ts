import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketRefund {
  id: string;
  ticketOrderId: string | null;
  ticketId: string | null;
  amount: number;
  currency: string;
  reason: string | null;
  status: string;
  requestedAt: string;
  resolvedAt: string | null;
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

export interface CreateTicketRefundRequest {
  ticketOrderId: string | null;
  ticketId: string | null;
  amount: number;
  currency: string;
  reason: string | null;
  status: string;
  resolvedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class TicketRefundService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticket-refunds';

  getAll(): Observable<TicketRefund[]> {
    return this.http.get<TicketRefund[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TicketRefund>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'requestedAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TicketRefund>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TicketRefund> {
    return this.http.get<TicketRefund>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTicketRefundRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTicketRefundRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
