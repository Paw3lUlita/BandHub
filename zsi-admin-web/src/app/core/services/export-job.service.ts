import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExportJob {
  id: string;
  module: string;
  entityName: string;
  status: string;
  requestedBy: string | null;
  filePath: string | null;
  createdAt: string;
  completedAt: string | null;
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

export interface CreateExportJobRequest {
  module: string;
  entityName: string;
  status: string;
  requestedBy: string | null;
  filePath: string | null;
  completedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class ExportJobService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/export-jobs';

  getAll(): Observable<ExportJob[]> {
    return this.http.get<ExportJob[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<ExportJob>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<ExportJob>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<ExportJob> {
    return this.http.get<ExportJob>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateExportJobRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateExportJobRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
