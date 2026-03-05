import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { TicketCodeService } from '../../../core/services/ticket-code.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'generatedAt', label: 'Wygenerowano' },
  { value: 'ticketId', label: 'Bilet' },
  { value: 'codeValue', label: 'Kod' },
  { value: 'codeType', label: 'Typ' },
  { value: 'status', label: 'Status' }
];

@Component({
  selector: 'app-ticket-code-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Kody Biletów</h2>
        <a routerLink="/admin/ticket-codes/new" class="btn btn-primary btn-sm">+ Dodaj Kod</a>
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
            <th>Bilet</th>
            <th>Kod</th>
            <th>Typ</th>
            <th>Status</th>
            <th>Wygenerowano</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.ticketId }}</td>
              <td class="font-mono">{{ item.codeValue }}</td>
              <td><span class="badge badge-ghost badge-sm">{{ item.codeType }}</span></td>
              <td><span class="badge badge-ghost badge-sm">{{ item.status }}</span></td>
              <td class="text-sm">{{ item.generatedAt | date:'short' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/ticket-codes', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="6" class="text-center py-4">Brak kodów</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TicketCodeListComponent {
  private service = inject(TicketCodeService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'generatedAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć kod?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
