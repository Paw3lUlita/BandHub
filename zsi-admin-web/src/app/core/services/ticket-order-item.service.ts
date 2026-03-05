import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketOrderItem {
  id: string;
  ticketOrderId: string;
  ticketPoolId: string;
  quantity: number;
  unitPrice: number;
  currency: string;
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

export interface CreateTicketOrderItemRequest {
  ticketOrderId: string;
  ticketPoolId: string;
  quantity: number;
  unitPrice: number;
  currency: string;
}

@Injectable({ providedIn: 'root' })
export class TicketOrderItemService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticket-order-items';

  getAll(): Observable<TicketOrderItem[]> {
    return this.http.get<TicketOrderItem[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TicketOrderItem>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'id',
      sortDir: params.sortDir ?? 'asc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TicketOrderItem>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TicketOrderItem> {
    return this.http.get<TicketOrderItem>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTicketOrderItemRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTicketOrderItemRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
