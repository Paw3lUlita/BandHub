import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ScanTicketRequest {
  codeValue: string;
  gateName?: string;
}

export interface ScanTicketResponse {
  valid: boolean;
  result: string;
  message: string;
  concertName: string | null;
  poolName: string | null;
  codeValue: string;
}

@Injectable({ providedIn: 'root' })
export class TicketScanService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/ticketing';

  scan(body: ScanTicketRequest): Observable<ScanTicketResponse> {
    return this.http.post<ScanTicketResponse>(`${this.apiUrl}/scan`, body);
  }
}
