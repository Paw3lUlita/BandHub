import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe, CurrencyPipe, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { OrderService, OrderStatus } from '../../core/services/order.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt', label: 'Data' },
  { value: 'userId', label: 'Klient' },
  { value: 'totalAmount', label: 'Kwota' },
  { value: 'status', label: 'Status' }
];

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, CurrencyPipe, NgClass, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Zamówienia</h2>
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
          <th>Data</th>
          <th>Klient</th>
          <th>Kwota</th>
          <th>Status</th>
          <th>Akcje</th> </tr>
        </thead>

        <tbody>
        @for (order of (pageData$ | async)?.content ?? []; track order.id) {
        <tr class="hover">
          <td>
            <div class="font-bold">{{ order.createdAt | date:'shortDate' }}</div>
          <div class="text-xs opacity-50">{{ order.createdAt | date:'shortTime' }}</div>
        </td>

        <td>
          <span class="badge badge-ghost badge-sm">{{ order.userId }}</span>
        </td>

        <td class="font-mono font-bold">
        {{ order.totalAmount | currency:order.currency:'symbol':'1.2-2' }}
        </td>

        <td>
          <div class="badge gap-2" [ngClass]="getStatusColor(order.status)">
        {{ order.status }}
        </div>
      </td>

      <td class="flex gap-2 items-center">

        @if (order.status === 'NEW') {
        <button class="btn btn-xs btn-success text-white"
                (click)="changeStatus(order.id, 'SHIPPED')"
                title="Wyślij">
          🚚
        </button>
        <button class="btn btn-xs btn-error text-white"
                (click)="changeStatus(order.id, 'CANCELLED')"
                title="Anuluj">
          ❌
        </button>
        } @else {
        <span class="text-xs text-gray-400 italic w-[60px] text-center">--</span>
        }

        <a [routerLink]="['/admin/orders', order.id]"
           class="btn btn-xs btn-ghost border-base-300 ml-2">
           👁️ Podgląd
        </a>
      </td>
    </tr>
        } @empty {
        <tr>
          <td colspan="5" class="text-center py-8 text-gray-500">
            Brak zamówień w systemie.
          </td>
        </tr>
        }
        </tbody>
      </table>
    </div>
  `
})
export class OrderListComponent {
  private orderService = inject(OrderService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.orderService.getOrdersPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir as 'asc' | 'desc', q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  // Helper do kolorów badge'y
  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.NEW: return 'badge-warning';
      case OrderStatus.SHIPPED: return 'badge-success text-white';
      case OrderStatus.CANCELLED: return 'badge-error text-white';
      default: return 'badge-ghost';
    }
  }

  changeStatus(orderId: string, statusStr: string) {
    const newStatus = statusStr as OrderStatus;

    if (confirm(`Czy zmienić status zamówienia na ${newStatus}?`)) {
      this.orderService.updateStatus(orderId, newStatus).subscribe({
        next: () => {
          this.params.update(prev => ({ ...prev }));
        },
        error: (err) => console.error('Błąd zmiany statusu', err)
      });
    }
  }
}
