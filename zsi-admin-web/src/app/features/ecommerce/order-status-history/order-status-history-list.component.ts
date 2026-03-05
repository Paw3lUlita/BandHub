import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { OrderStatusHistoryService } from '../../../core/services/order-status-history.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'changedAt', label: 'Data' },
  { value: 'orderId', label: 'Zamówienie' },
  { value: 'newStatus', label: 'Nowy status' }
];

@Component({
  selector: 'app-order-status-history-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Historia Statusów Zamówień</h2>
        <a routerLink="/admin/order-status-history/new" class="btn btn-primary btn-sm">+ Dodaj</a>
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
            <th>Stary → Nowy</th>
            <th>Zmienił</th>
            <th>Data</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.orderId }}</td>
              <td><span class="badge badge-ghost badge-sm">{{ item.oldStatus || '-' }}</span> → <span class="badge badge-primary badge-sm">{{ item.newStatus }}</span></td>
              <td>{{ item.changedBy || '-' }}</td>
              <td class="text-sm">{{ item.changedAt | date:'short' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/order-status-history', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="5" class="text-center py-4">Brak wpisów</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class OrderStatusHistoryListComponent {
  private service = inject(OrderStatusHistoryService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'changedAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć wpis?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
