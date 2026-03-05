import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketCode {
  id: string;
  ticketId: string;
  codeValue: string;
  codeType: string;
  status: string;
  generatedAt: string | null;
  activatedAt: string | null;
  usedAt: string | null;
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

export interface CreateTicketCodeRequest {
  ticketId: string;
  codeValue: string;
  codeType: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class TicketCodeService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticket-codes';

  getAll(): Observable<TicketCode[]> {
    return this.http.get<TicketCode[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TicketCode>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'generatedAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TicketCode>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TicketCode> {
    return this.http.get<TicketCode>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTicketCodeRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTicketCodeRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
