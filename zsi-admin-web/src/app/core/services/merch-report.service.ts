import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MerchSalesSnapshot {
  orderCount: number;
  totalRevenue: number;
  currency: string;
  totalUnits: number;
}

@Injectable({ providedIn: 'root' })
export class MerchReportService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/reports/merch';

  getSalesSnapshot(from?: string, to?: string): Observable<MerchSalesSnapshot> {
    const params = new URLSearchParams();
    if (from) params.set('from', from);
    if (to) params.set('to', to);
    const qs = params.toString();
    return this.http.get<MerchSalesSnapshot>(`${this.apiUrl}/sales-snapshot${qs ? '?' + qs : ''}`);
  }
}
