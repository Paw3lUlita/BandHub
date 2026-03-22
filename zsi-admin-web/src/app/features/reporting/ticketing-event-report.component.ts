import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Concert, ConcertService } from '../../core/services/concert.service';
import {
  TicketingEventReportService,
  TicketingEventSnapshot
} from '../../core/services/ticketing-event-report.service';

@Component({
  selector: 'app-ticketing-event-report',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, DecimalPipe, FormsModule],
  template: `
    <div class="max-w-4xl">
      <h2 class="text-2xl font-bold mb-6">Raport finansowy wydarzenia (ticketing)</h2>

      <div class="flex flex-wrap gap-4 mb-6 p-4 bg-base-200 rounded-lg items-end">
        <div class="form-control">
          <label class="label"><span class="label-text">Koncert</span></label>
          <select class="select select-bordered select-sm min-w-[240px]" [(ngModel)]="selectedConcertId" (ngModelChange)="load()">
            <option value="">— wybierz —</option>
            @for (c of concerts(); track c.id) {
              <option [value]="c.id">{{ c.name }} — {{ c.date | date:'short' }}</option>
            }
          </select>
        </div>
        <button class="btn btn-sm btn-primary" (click)="load()" [disabled]="!selectedConcertId">Odśwież</button>
      </div>

      @if (loading()) {
        <div class="flex justify-center p-10">
          <span class="loading loading-spinner loading-lg"></span>
        </div>
      } @else if (snapshot()) {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Sprzedane bilety</h3>
              <p class="text-3xl font-bold">{{ snapshot()!.soldTickets }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Przychód</h3>
              <p class="text-3xl font-bold">{{ snapshot()!.totalRevenue | currency:snapshot()!.currency:'symbol':'1.2-2' }}</p>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Obłożenie</h3>
              <p class="text-3xl font-bold">{{ snapshot()!.occupancyPercent | number:'1.1-1' }}%</p>
              <p class="text-xs text-gray-500">Pozostało w pulach: {{ snapshot()!.remainingTickets }}</p>
            </div>
          </div>
        </div>
        <p class="mt-4 text-sm text-gray-500">Pojemność miejsca: {{ snapshot()!.venueCapacity }}</p>
      }
    </div>
  `
})
export class TicketingEventReportComponent implements OnInit {
  private concertService = inject(ConcertService);
  private reportService = inject(TicketingEventReportService);

  concerts = signal<Concert[]>([]);
  selectedConcertId = '';
  loading = signal(false);
  snapshot = signal<TicketingEventSnapshot | null>(null);

  ngOnInit() {
    this.concertService.getAll().subscribe((list) => this.concerts.set(list));
  }

  load() {
    if (!this.selectedConcertId) {
      this.snapshot.set(null);
      return;
    }
    this.loading.set(true);
    this.reportService.getEventSummary(this.selectedConcertId).subscribe({
      next: (data) => {
        this.snapshot.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
