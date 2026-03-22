import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TicketingEventSnapshot {
  concertId: string;
  concertName: string;
  soldTickets: number;
  remainingTickets: number;
  totalRevenue: number;
  currency: string;
  venueCapacity: number;
  occupancyPercent: number;
}

@Injectable({ providedIn: 'root' })
export class TicketingEventReportService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/reports/ticketing';

  getEventSummary(concertId: string): Observable<TicketingEventSnapshot> {
    const p = new URLSearchParams({ concertId });
    return this.http.get<TicketingEventSnapshot>(`${this.apiUrl}/event-summary?${p.toString()}`);
  }
}
