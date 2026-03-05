import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentTransaction {
  id: string;
  paymentId: string;
  eventType: string;
  externalStatus: string | null;
  payload: string | null;
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

export interface CreatePaymentTransactionRequest {
  paymentId: string;
  eventType: string;
  externalStatus: string | null;
  payload: string | null;
}

@Injectable({ providedIn: 'root' })
export class PaymentTransactionService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/payment-transactions';

  getAll(): Observable<PaymentTransaction[]> {
    return this.http.get<PaymentTransaction[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<PaymentTransaction>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<PaymentTransaction>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<PaymentTransaction> {
    return this.http.get<PaymentTransaction>(`${this.apiUrl}/${id}`);
  }

  create(req: CreatePaymentTransactionRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreatePaymentTransactionRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
