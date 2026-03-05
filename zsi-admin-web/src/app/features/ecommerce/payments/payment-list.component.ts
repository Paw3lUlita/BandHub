import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { PaymentService } from '../../../core/services/payment.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const PAYMENT_SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt', label: 'Data' },
  { value: 'orderId', label: 'Zamówienie' },
  { value: 'status', label: 'Status' },
  { value: 'amount', label: 'Kwota' }
];

@Component({
  selector: 'app-payment-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Płatności</h2>
        <a routerLink="/admin/payments/new" class="btn btn-primary btn-sm">+ Dodaj Płatność</a>
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
            <th>Zamówienie</th>
            <th>Status</th>
            <th>Kwota</th>
            <th>Data</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.orderId }}</td>
              <td><span class="badge badge-ghost badge-sm">{{ item.status }}</span></td>
              <td class="font-mono">{{ item.amount | currency:item.currency:'symbol':'1.2-2' }}</td>
              <td class="text-sm">{{ item.createdAt | date:'short' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/payments', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="5" class="text-center py-4">Brak płatności</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class PaymentListComponent {
  private service = inject(PaymentService);
  sortOptions = PAYMENT_SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć płatność?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
