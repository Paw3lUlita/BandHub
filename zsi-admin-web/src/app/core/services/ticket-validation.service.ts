import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketValidation {
  id: string;
  ticketCodeId: string;
  validatedBy: string | null;
  gateName: string | null;
  validationResult: string;
  validationTime: string;
  details: string | null;
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

export interface CreateTicketValidationRequest {
  ticketCodeId: string;
  validatedBy: string | null;
  gateName: string | null;
  validationResult: string;
  details: string | null;
}

@Injectable({ providedIn: 'root' })
export class TicketValidationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticket-validations';

  getAll(): Observable<TicketValidation[]> {
    return this.http.get<TicketValidation[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<TicketValidation>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'validationTime',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<TicketValidation>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<TicketValidation> {
    return this.http.get<TicketValidation>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateTicketValidationRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateTicketValidationRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
