import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MerchSalesSnapshot } from './merch-report.service';
import { TicketingEventSnapshot } from './ticketing-event-report.service';
import { TourProfitability } from './logistics.service';

export type BusinessReportType =
  | 'MERCH'
  | 'TICKETING_EVENT'
  | 'TOUR_PROFITABILITY'
  | 'TOUR_SETTLEMENT_DOCX';

export interface TourSettlementDocxPreviewPayload {
  settlementPresent: boolean;
  settlement: Record<string, unknown> | null;
  profitability: TourProfitability;
  activeTemplateId: string | null;
  activeTemplateName: string | null;
}

export interface BusinessReportPreviewResponse {
  reportType: BusinessReportType;
  payload:
    | MerchSalesSnapshot
    | TicketingEventSnapshot
    | TourProfitability
    | TourSettlementDocxPreviewPayload;
}

@Injectable({ providedIn: 'root' })
export class BusinessReportService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/admin/reports/business';

  preview(
    type: BusinessReportType,
    opts: { from?: string; to?: string; concertId?: string; tourId?: string }
  ): Observable<BusinessReportPreviewResponse> {
    let params = new HttpParams().set('type', type);
    if (opts.from) params = params.set('from', opts.from);
    if (opts.to) params = params.set('to', opts.to);
    if (opts.concertId) params = params.set('concertId', opts.concertId);
    if (opts.tourId) params = params.set('tourId', opts.tourId);
    return this.http.get<BusinessReportPreviewResponse>(`${this.baseUrl}/preview`, { params });
  }

  export(
    type: BusinessReportType,
    format: 'pdf' | 'xlsx' | 'docx',
    opts: { from?: string; to?: string; concertId?: string; tourId?: string }
  ): Observable<Blob> {
    let params = new HttpParams().set('type', type).set('format', format);
    if (opts.from) params = params.set('from', opts.from);
    if (opts.to) params = params.set('to', opts.to);
    if (opts.concertId) params = params.set('concertId', opts.concertId);
    if (opts.tourId) params = params.set('tourId', opts.tourId);
    return this.http.get(`${this.baseUrl}/export`, { params, responseType: 'blob' });
  }
}
