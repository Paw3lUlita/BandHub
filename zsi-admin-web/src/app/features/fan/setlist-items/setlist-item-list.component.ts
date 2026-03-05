import { Component, inject, signal } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { SetlistItemService } from '../../../core/services/setlist-item.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'songOrder', label: 'Kolejność' },
  { value: 'setlistId', label: 'Setlista' },
  { value: 'songTitle', label: 'Utwór' }
];

@Component({
  selector: 'app-setlist-item-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Pozycje Setlist</h2>
        <a routerLink="/admin/setlist-items/new" class="btn btn-primary btn-sm">+ Dodaj</a>
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
            <th>Setlista</th>
            <th>Utwór</th>
            <th>Kolejność</th>
            <th>Czas (s)</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="text-xs font-mono">{{ item.setlistId }}</td>
              <td class="font-bold">{{ item.songTitle }}</td>
              <td>{{ item.songOrder }}</td>
              <td>{{ item.durationSeconds ?? '-' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/setlist-items', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
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
export class SetlistItemListComponent {
  private service = inject(SetlistItemService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'songOrder', sortDir: 'asc', q: '' });
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
