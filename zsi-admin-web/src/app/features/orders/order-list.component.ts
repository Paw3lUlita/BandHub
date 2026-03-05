import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe, CurrencyPipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

type OrderListParams = ListPageParams & { status?: OrderStatus };

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, CurrencyPipe, NgClass, FormsModule, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Zamówienia</h2>
      </div>
      <div class="flex flex-wrap items-center gap-4 mb-4 p-3 bg-base-200 rounded-lg">
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium">Status:</label>
          <select class="select select-bordered select-sm" [ngModel]="params().status" (ngModelChange)="onStatusChange($event)">
            <option [ngValue]="undefined">Wszystkie</option>
            <option [ngValue]="OrderStatus.NEW">Nowe</option>
            <option [ngValue]="OrderStatus.PAID">Opłacone</option>
            <option [ngValue]="OrderStatus.SHIPPED">Wysłane</option>
            <option [ngValue]="OrderStatus.DELIVERED">Dostarczone</option>
            <option [ngValue]="OrderStatus.CANCELLED">Anulowane</option>
          </select>
        </div>
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
        @for (t of getAvailableTransitions(order.status); track t.next) {
          <button class="btn btn-xs"
                  [class.btn-success]="t.next !== 'CANCELLED'"
                  [class.btn-error]="t.next === 'CANCELLED'"
                  [class.text-white]="true"
                  (click)="changeStatus(order.id, t.next)"
                  [title]="t.label">
            {{ t.next === 'CANCELLED' ? '❌' : '✓' }} {{ t.label }}
          </button>
        }
        @if (getAvailableTransitions(order.status).length === 0 && order.status !== 'CANCELLED' && order.status !== 'DELIVERED') {
          <span class="text-xs text-gray-400 italic">--</span>
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

  OrderStatus = OrderStatus;

  params = signal<OrderListParams>({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc', q: '', status: undefined });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.orderService.getOrdersPage({
      page: p.page,
      size: p.size,
      sortBy: p.sortBy,
      sortDir: p.sortDir as 'asc' | 'desc',
      q: p.q,
      status: p.status
    }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set({ ...this.params(), ...p });
  }

  onStatusChange(status: OrderStatus | undefined) {
    this.params.update(prev => ({ ...prev, status, page: 0 }));
  }

  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.NEW: return 'badge-warning';
      case OrderStatus.PAID: return 'badge-info text-white';
      case OrderStatus.SHIPPED: return 'badge-primary text-white';
      case OrderStatus.DELIVERED: return 'badge-success text-white';
      case OrderStatus.CANCELLED: return 'badge-error text-white';
      default: return 'badge-ghost';
    }
  }

  getAvailableTransitions(status: OrderStatus): { next: OrderStatus; label: string }[] {
    switch (status) {
      case OrderStatus.NEW: return [{ next: OrderStatus.PAID, label: 'Opłacone' }, { next: OrderStatus.CANCELLED, label: 'Anuluj' }];
      case OrderStatus.PAID: return [{ next: OrderStatus.SHIPPED, label: 'Wysłano' }, { next: OrderStatus.CANCELLED, label: 'Anuluj' }];
      case OrderStatus.SHIPPED: return [{ next: OrderStatus.DELIVERED, label: 'Dostarczono' }];
      default: return [];
    }
  }

  changeStatus(orderId: string, newStatus: OrderStatus) {
    if (confirm(`Czy zmienić status zamówienia na ${newStatus}?`)) {
      this.orderService.updateStatus(orderId, newStatus).subscribe({
        next: () => this.params.update(prev => ({ ...prev })),
        error: (err) => console.error('Błąd zmiany statusu', err)
      });
    }
  }
}
