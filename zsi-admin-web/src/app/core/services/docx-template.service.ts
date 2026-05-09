import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export const DOCX_MODULE_TOUR_SETTLEMENT = 'TOUR_SETTLEMENT';

export interface DocxTemplate {
  id: string;
  name: string;
  moduleCode: string;
  templateVersion: number;
  active: boolean;
  filePath: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class DocxTemplateService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/admin/reports/docx-templates';

  list(moduleCode?: string): Observable<DocxTemplate[]> {
    let params = new HttpParams();
    if (moduleCode) {
      params = params.set('moduleCode', moduleCode);
    }
    return this.http.get<DocxTemplate[]>(this.baseUrl, { params });
  }

  upload(name: string, moduleCode: string, file: File): Observable<DocxTemplate> {
    const fd = new FormData();
    fd.append('name', name);
    fd.append('moduleCode', moduleCode);
    fd.append('file', file);
    return this.http.post<DocxTemplate>(this.baseUrl, fd);
  }

  activate(id: string): Observable<DocxTemplate> {
    return this.http.patch<DocxTemplate>(`${this.baseUrl}/${id}/activate`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
