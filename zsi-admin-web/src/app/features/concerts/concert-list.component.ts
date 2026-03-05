import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { ConcertService } from '../../core/services/concert.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'date', label: 'Data' },
  { value: 'name', label: 'Nazwa' },
  { value: 'venueName', label: 'Miejsce' },
  { value: 'city', label: 'Miasto' }
];

@Component({
  selector: 'app-concert-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Koncerty</h2>
        <a routerLink="/admin/concerts/new" class="btn btn-primary btn-sm">
          + Dodaj Koncert
        </a>
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
            <th>Nazwa</th>
            <th>Data</th>
            <th>Miejsce</th>
            <th>Miasto</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (concert of (pageData$ | async)?.content ?? []; track concert.id) {
            <tr class="hover">
              <td class="font-bold">{{ concert.name }}</td>
              <td>
                <div>{{ concert.date | date:'shortDate' }}</div>
                <div class="text-xs opacity-50">{{ concert.date | date:'shortTime' }}</div>
              </td>
              <td>{{ concert.venueName }}</td>
              <td>
                <span class="badge badge-ghost">{{ concert.city }}</span>
              </td>
              <td>
                <a [routerLink]="['/admin/concerts', concert.id]" class="btn btn-ghost btn-xs">
                   Szczegóły
                </a>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="text-center py-8 text-gray-500">
                Brak zaplanowanych koncertów.
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class ConcertListComponent {
  private concertService = inject(ConcertService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'date', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.concertService.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir as 'asc' | 'desc', q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }
}
