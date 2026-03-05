import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { TicketOrderItemService } from '../../../core/services/ticket-order-item.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'ticketOrderId', label: 'Zamówienie' },
  { value: 'ticketPoolId', label: 'Pula biletów' },
  { value: 'quantity', label: 'Ilość' },
  { value: 'unitPrice', label: 'Cena jedn.' }
];

@Component({
  selector: 'app-ticket-order-item-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Pozycje Zamówień Biletów</h2>
        <a routerLink="/admin/ticket-order-items/new" class="btn btn-primary btn-sm">+ Dodaj</a>
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
            <th>Pula biletów</th>
            <th>Ilość</th>
            <th>Cena jedn.</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.ticketOrderId }}</td>
              <td class="text-xs font-mono">{{ item.ticketPoolId }}</td>
              <td>{{ item.quantity }}</td>
              <td class="font-mono">{{ item.unitPrice | currency:item.currency:'symbol':'1.2-2' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/ticket-order-items', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="5" class="text-center py-4">Brak pozycji</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TicketOrderItemListComponent {
  private service = inject(TicketOrderItemService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'ticketOrderId', sortDir: 'asc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć pozycję?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
