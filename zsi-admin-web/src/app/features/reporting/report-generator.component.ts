import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  BusinessReportPreviewResponse,
  BusinessReportService,
  BusinessReportType
} from '../../core/services/business-report.service';
import { Concert, ConcertService } from '../../core/services/concert.service';
import { Tour, LogisticsService } from '../../core/services/logistics.service';
import { MerchSalesSnapshot } from '../../core/services/merch-report.service';
import { TicketingEventSnapshot } from '../../core/services/ticketing-event-report.service';
import { TourProfitability } from '../../core/services/logistics.service';

@Component({
  selector: 'app-report-generator',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe, DecimalPipe],
  template: `
    <div class="max-w-5xl">
      <h2 class="text-2xl font-bold mb-2">Generator raportów biznesowych</h2>
      <p class="text-sm text-base-content/70 mb-6">
        Podgląd danych (JSON) oraz pobranie pliku PDF lub Excel — zgodnie z wymaganiami raportowymi ZSI.
      </p>

      <div class="flex flex-wrap gap-4 mb-6 p-4 bg-base-200 rounded-lg items-end">
        <div class="form-control">
          <label class="label"><span class="label-text">Typ raportu</span></label>
          <select
            class="select select-bordered select-sm min-w-[260px]"
            [(ngModel)]="reportType"
            (ngModelChange)="onTypeChange()"
          >
            <option value="MERCH">Merch — podsumowanie sprzedaży</option>
            <option value="TICKETING_EVENT">Ticketing — wydarzenie (koncert)</option>
            <option value="TOUR_PROFITABILITY">Logistyka — rentowność trasy</option>
          </select>
        </div>

        @if (reportType === 'MERCH') {
          <div class="flex items-center gap-2">
            <label class="text-sm font-medium">Od:</label>
            <input type="date" class="input input-bordered input-sm" [(ngModel)]="from" />
          </div>
          <div class="flex items-center gap-2">
            <label class="text-sm font-medium">Do:</label>
            <input type="date" class="input input-bordered input-sm" [(ngModel)]="to" />
          </div>
        }

        @if (reportType === 'TICKETING_EVENT') {
          <div class="form-control">
            <label class="label"><span class="label-text">Koncert</span></label>
            <select class="select select-bordered select-sm min-w-[260px]" [(ngModel)]="concertId">
              <option value="">— wybierz —</option>
              @for (c of concerts(); track c.id) {
                <option [value]="c.id">{{ c.name }} — {{ c.date | date: 'short' }}</option>
              }
            </select>
          </div>
        }

        @if (reportType === 'TOUR_PROFITABILITY') {
          <div class="form-control">
            <label class="label"><span class="label-text">Trasa</span></label>
            <select class="select select-bordered select-sm min-w-[260px]" [(ngModel)]="tourId">
              <option value="">— wybierz —</option>
              @for (t of tours(); track t.id) {
                <option [value]="t.id">{{ t.name }}</option>
              }
            </select>
          </div>
        }

        <button class="btn btn-sm btn-primary" (click)="loadPreview()" [disabled]="!canPreview() || loading()">
          Podgląd
        </button>
        <button
          class="btn btn-sm btn-outline"
          (click)="download('pdf')"
          [disabled]="!canPreview() || loading()"
        >
          Pobierz PDF
        </button>
        <button
          class="btn btn-sm btn-outline"
          (click)="download('xlsx')"
          [disabled]="!canPreview() || loading()"
        >
          Pobierz Excel
        </button>
      </div>

      @if (loading()) {
        <div class="flex justify-center p-10">
          <span class="loading loading-spinner loading-lg"></span>
        </div>
      } @else if (merchPreview(); as m) {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Liczba zamówień</h3>
              <p class="text-3xl font-bold">{{ m.orderCount }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Przychód</h3>
              <p class="text-3xl font-bold">{{ m.totalRevenue | currency: m.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Sztuki</h3>
              <p class="text-3xl font-bold">{{ m.totalUnits }}</p>
            </div>
          </div>
        </div>
      } @else if (ticketingPreview(); as t) {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Sprzedane bilety</h3>
              <p class="text-3xl font-bold">{{ t.soldTickets }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Przychód</h3>
              <p class="text-3xl font-bold">{{ t.totalRevenue | currency: t.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Obłożenie</h3>
              <p class="text-3xl font-bold">{{ t.occupancyPercent | number: '1.1-1' }}%</p>
              <p class="text-xs text-base-content/50">Pozostało w pulach: {{ t.remainingTickets }}</p>
            </div>
          </div>
        </div>
        <p class="mt-4 text-sm text-base-content/60">Pojemność miejsca: {{ t.venueCapacity }}</p>
      } @else if (tourPreview(); as tr) {
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Koszty łącznie</h3>
              <p class="text-2xl font-bold">{{ tr.totalCosts | currency: tr.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Przychód z biletów</h3>
              <p class="text-2xl font-bold">{{ tr.ticketRevenue | currency: tr.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Przychody ręczne</h3>
              <p class="text-2xl font-bold">{{ tr.manualRevenue | currency: tr.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Przychód łącznie</h3>
              <p class="text-2xl font-bold">{{ tr.totalRevenue | currency: tr.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl md:col-span-2 lg:col-span-3">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Bilans</h3>
              <p class="text-3xl font-bold">{{ tr.balance | currency: tr.currency : 'symbol' : '1.2-2' }}</p>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ReportGeneratorComponent implements OnInit {
  private businessReports = inject(BusinessReportService);
  private concertService = inject(ConcertService);
  private logisticsService = inject(LogisticsService);

  reportType: BusinessReportType = 'MERCH';
  from = '';
  to = '';
  concertId = '';
  tourId = '';

  concerts = signal<Concert[]>([]);
  tours = signal<Tour[]>([]);
  loading = signal(false);
  preview = signal<BusinessReportPreviewResponse | null>(null);

  merchPreview = computed(() => {
    const p = this.preview();
    if (!p || p.reportType !== 'MERCH') return null;
    return p.payload as MerchSalesSnapshot;
  });

  ticketingPreview = computed(() => {
    const p = this.preview();
    if (!p || p.reportType !== 'TICKETING_EVENT') return null;
    return p.payload as TicketingEventSnapshot;
  });

  tourPreview = computed(() => {
    const p = this.preview();
    if (!p || p.reportType !== 'TOUR_PROFITABILITY') return null;
    return p.payload as TourProfitability;
  });

  ngOnInit() {
    this.concertService.getAll().subscribe((list) => this.concerts.set(list));
    this.logisticsService.getAllTours().subscribe((list) => this.tours.set(list));
  }

  onTypeChange() {
    this.preview.set(null);
  }

  canPreview(): boolean {
    switch (this.reportType) {
      case 'MERCH':
        return true;
      case 'TICKETING_EVENT':
        return !!this.concertId;
      case 'TOUR_PROFITABILITY':
        return !!this.tourId;
      default:
        return false;
    }
  }

  loadPreview() {
    if (!this.canPreview()) return;
    this.loading.set(true);
    this.businessReports.preview(this.reportType, this.opts()).subscribe({
      next: (data) => {
        this.preview.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  download(format: 'pdf' | 'xlsx') {
    if (!this.canPreview()) return;
    this.loading.set(true);
    this.businessReports.export(this.reportType, format, this.opts()).subscribe({
      next: (blob) => {
        const ext = format === 'pdf' ? 'pdf' : 'xlsx';
        const name = `${this.reportType.toLowerCase()}-report.${ext}`;
        this.triggerDownload(blob, name);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  private opts() {
    return {
      from: this.from || undefined,
      to: this.to || undefined,
      concertId: this.concertId || undefined,
      tourId: this.tourId || undefined
    };
  }

  private triggerDownload(blob: Blob, filename: string) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
}
