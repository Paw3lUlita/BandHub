import { Component, inject, OnInit, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { combineLatest, switchMap } from 'rxjs';
import { TicketOrderService } from '../../../core/services/ticket-order.service';
import { ConcertService, Concert } from '../../../core/services/concert.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt', label: 'Data' },
  { value: 'userId', label: 'Użytkownik' },
  { value: 'concertId', label: 'Koncert' },
  { value: 'status', label: 'Status' },
  { value: 'totalAmount', label: 'Kwota' }
];

@Component({
  selector: 'app-ticket-order-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, DatePipe, RouterLink, ListPageControlsComponent, FormsModule],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Zamówienia Biletów</h2>
        <a routerLink="/admin/ticket-orders/new" class="btn btn-primary btn-sm">+ Dodaj</a>
      </div>
      <div class="flex flex-wrap items-center gap-2 mb-4">
        <label class="text-sm font-medium">Koncert:</label>
        <select
          class="select select-bordered select-sm min-w-[200px]"
          [ngModel]="concertFilter()"
          (ngModelChange)="onConcertFilterChange($event)"
        >
          <option value="">Wszystkie</option>
          @for (c of concerts(); track c.id) {
            <option [value]="c.id">{{ c.name }}</option>
          }
        </select>
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
            <th>Użytkownik</th>
            <th>Koncert</th>
            <th>Status</th>
            <th>Kwota</th>
            <th>Data</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td>{{ item.username || item.userId }}</td>
              <td>{{ item.concertName || item.concertId }}</td>
              <td><span class="badge badge-ghost badge-sm">{{ item.status }}</span></td>
              <td class="font-mono">{{ item.totalAmount | currency:item.currency:'symbol':'1.2-2' }}</td>
              <td class="text-sm">{{ item.createdAt | date:'short' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/ticket-orders', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="6" class="text-center py-4">Brak zamówień</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TicketOrderListComponent implements OnInit {
  private service = inject(TicketOrderService);
  private concertService = inject(ConcertService);
  sortOptions = SORT_OPTIONS;

  concerts = signal<Concert[]>([]);
  concertFilter = signal<string>('');

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc', q: '' });
  pageData$ = combineLatest([toObservable(this.params), toObservable(this.concertFilter)]).pipe(
    switchMap(([p, cid]) =>
      this.service.getPage({
        page: p.page,
        size: p.size,
        sortBy: p.sortBy,
        sortDir: p.sortDir,
        q: p.q,
        concertId: cid || undefined
      })
    )
  );

  ngOnInit() {
    this.concertService.getAll().subscribe((list) => this.concerts.set(list));
  }

  onConcertFilterChange(value: string) {
    this.concertFilter.set(value);
    this.params.update((prev) => ({ ...prev, page: 0 }));
  }

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć zamówienie?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
