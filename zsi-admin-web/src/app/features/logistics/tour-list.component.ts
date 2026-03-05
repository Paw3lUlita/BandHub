import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { LogisticsService } from '../../core/services/logistics.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'startDate', label: 'Data startu' },
  { value: 'endDate', label: 'Data końca' },
  { value: 'name', label: 'Nazwa' }
];

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Trasy Koncertowe</h2>
        <a routerLink="/admin/logistics/new" class="btn btn-primary btn-sm">
          + Zaplanuj Trasę
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
            <th>Nazwa Trasy</th>
            <th>Start</th>
            <th>Koniec</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (tour of (pageData$ | async)?.content ?? []; track tour.id) {
            <tr class="hover">
              <td class="font-bold text-lg">{{ tour.name }}</td>
              <td>{{ tour.startDate | date:'shortDate' }}</td>
              <td>{{ tour.endDate | date:'shortDate' }}</td>
              <td>
                <a [routerLink]="['/admin/logistics', tour.id]" class="btn btn-ghost btn-sm text-primary">
                  Panel Logistyki
                </a>
                <button class="btn btn-ghost btn-sm text-error" (click)="deleteTour(tour.id)">
                  Usun
                </button>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="4" class="text-center py-8 text-gray-500">
                Brak zaplanowanych tras.
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TourListComponent {
  private service = inject(LogisticsService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'startDate', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getToursPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir as 'asc' | 'desc', q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  deleteTour(id: string) {
    if (!confirm('Czy na pewno chcesz usunac trase?')) {
      return;
    }

    this.service.deleteTour(id).subscribe(() => {
      this.params.update(prev => ({ ...prev }));
    });
  }
}
