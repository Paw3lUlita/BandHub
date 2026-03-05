import { Component, inject, OnInit, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchReportService, MerchSalesSnapshot } from '../../core/services/merch-report.service';

@Component({
  selector: 'app-merch-report',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, FormsModule],
  template: `
    <div class="max-w-4xl">
      <h2 class="text-2xl font-bold mb-6">Raport sprzedaży merchu</h2>

      <div class="flex flex-wrap gap-4 mb-6 p-4 bg-base-200 rounded-lg">
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium">Od:</label>
          <input type="date" class="input input-bordered input-sm" [(ngModel)]="from" (ngModelChange)="load()" />
        </div>
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium">Do:</label>
          <input type="date" class="input input-bordered input-sm" [(ngModel)]="to" (ngModelChange)="load()" />
        </div>
        <button class="btn btn-sm btn-primary" (click)="load()">Odśwież</button>
      </div>

      @if (loading()) {
        <div class="flex justify-center p-10">
          <span class="loading loading-spinner loading-lg"></span>
        </div>
      } @else if (snapshot()) {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Liczba zamówień</h3>
              <p class="text-3xl font-bold">{{ snapshot()!.orderCount }}</p>
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
              <h3 class="card-title text-sm uppercase text-gray-400">Sztuki</h3>
              <p class="text-3xl font-bold">{{ snapshot()!.totalUnits }}</p>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class MerchReportComponent implements OnInit {
  private reportService = inject(MerchReportService);

  from = '';
  to = '';
  loading = signal(false);
  snapshot = signal<MerchSalesSnapshot | null>(null);

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.reportService.getSalesSnapshot(this.from || undefined, this.to || undefined).subscribe({
      next: (data) => {
        this.snapshot.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
