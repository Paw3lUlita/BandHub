import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TourSettlement, TourSettlementService } from '../../../core/services/tour-settlement.service';

@Component({
  selector: 'app-tour-settlement-detail',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe],
  template: `
    <div class="max-w-2xl mx-auto space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold">Rozliczenie trasy</h2>
        <a routerLink="/admin/tour-settlements" class="btn btn-ghost btn-sm">Lista</a>
      </div>
      @if (item(); as s) {
        <div class="card bg-base-100 shadow-xl">
          <div class="card-body space-y-2">
            <p><span class="opacity-70">Trasa:</span> {{ s.tourName || s.tourId }}</p>
            <p><span class="opacity-70">Rozliczył:</span> {{ s.settledBy || '-' }}</p>
            <p><span class="opacity-70">Data:</span> {{ s.settledAt | date:'short' }}</p>
            <p><span class="opacity-70">Koszty:</span> {{ s.totalCosts | currency:(s.currency ?? 'PLN'):'symbol':'1.2-2' }}</p>
            <p><span class="opacity-70">Przychody:</span> {{ s.totalRevenue | currency:(s.currency ?? 'PLN'):'symbol':'1.2-2' }}</p>
            <p class="text-lg font-bold"><span class="opacity-70">Saldo:</span> {{ s.balance | currency:(s.currency ?? 'PLN'):'symbol':'1.2-2' }}</p>
            @if (s.notes) {
              <div class="divider"></div>
              <p class="whitespace-pre-wrap">{{ s.notes }}</p>
            }
            <div class="card-actions justify-end pt-4">
              <a [routerLink]="['/admin/logistics', s.tourId]" class="btn btn-primary btn-sm">Panel trasy</a>
            </div>
          </div>
        </div>
      } @else {
        <span class="loading loading-spinner loading-lg"></span>
      }
    </div>
  `
})
export class TourSettlementDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private service = inject(TourSettlementService);

  item = signal<TourSettlement | null>(null);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.service.getOne(id).subscribe(x => this.item.set(x));
    }
  }
}
