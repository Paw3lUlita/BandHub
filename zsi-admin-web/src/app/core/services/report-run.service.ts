import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReportRun {
  id: string;
  reportName: string;
  requestedBy: string | null;
  parametersJson: string | null;
  status: string;
  fileFormat: string | null;
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

export interface CreateReportRunRequest {
  reportName: string;
  requestedBy: string | null;
  parametersJson: string | null;
  status: string;
  fileFormat: string | null;
  completedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class ReportRunService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/report-runs';

  getAll(): Observable<ReportRun[]> {
    return this.http.get<ReportRun[]>(this.apiUrl);
  }

  getPage(params: { page?: number; size?: number; sortBy?: string; sortDir?: string; q?: string }): Observable<PageResponse<ReportRun>> {
    const p = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });
    return this.http.get<PageResponse<ReportRun>>(`${this.apiUrl}/page?${p}`);
  }

  getOne(id: string): Observable<ReportRun> {
    return this.http.get<ReportRun>(`${this.apiUrl}/${id}`);
  }

  create(req: CreateReportRunRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, req);
  }

  update(id: string, req: CreateReportRunRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
