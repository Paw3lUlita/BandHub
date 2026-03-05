import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { TourSettlementService } from '../../../core/services/tour-settlement.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'settledAt', label: 'Data' },
  { value: 'tourId', label: 'Trasa' },
  { value: 'settledBy', label: 'Rozliczył' }
];

@Component({
  selector: 'app-tour-settlement-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Rozliczenia Tras</h2>
        <a routerLink="/admin/tour-settlements/new" class="btn btn-primary btn-sm">+ Dodaj</a>
      </div>
      <app-list-page-controls
        [params]="params()"
        [sortOptions]="sortOptions"
        [totalElements]="(pageData$ | async)?.totalElements ?? -1"
        [totalPages]="(pageData$ | async)?.totalPages ?? 0"
        (paramsChange)="onParamsChange($event)"
      />
      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>Trasa</th>
            <th>Rozliczył</th>
            <th>Data</th>
            <th>Koszty</th>
            <th>Przychody</th>
            <th>Saldo</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.tourId }}</td>
              <td>{{ item.settledBy || '-' }}</td>
              <td class="text-sm">{{ item.settledAt | date:'short' }}</td>
              <td class="font-mono">{{ item.totalCosts != null ? (item.totalCosts | currency:(item.currency ?? 'PLN'):'symbol':'1.2-2') : '-' }}</td>
              <td class="font-mono">{{ item.totalRevenue != null ? (item.totalRevenue | currency:(item.currency ?? 'PLN'):'symbol':'1.2-2') : '-' }}</td>
              <td class="font-mono font-bold">{{ item.balance != null ? (item.balance | currency:(item.currency ?? 'PLN'):'symbol':'1.2-2') : '-' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/tour-settlements', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="7" class="text-center py-4">Brak rozliczeń</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TourSettlementListComponent {
  private service = inject(TourSettlementService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'settledAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć rozliczenie?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
