import { Component, inject, signal } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { TourRevenueCategoryService } from '../../../core/services/tour-revenue-category.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'name', label: 'Nazwa' },
  { value: 'code', label: 'Kod' }
];

@Component({
  selector: 'app-tour-revenue-category-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Kategorie Przychodów Trasy</h2>
        <a routerLink="/admin/tour-revenue-categories/new" class="btn btn-primary btn-sm">+ Dodaj</a>
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
            <th>Kod</th>
            <th>Nazwa</th>
            <th>Aktywna</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="font-mono">{{ item.code }}</td>
              <td class="font-bold">{{ item.name }}</td>
              <td>@if (item.active) { <span class="badge badge-success badge-sm">Tak</span> } @else { <span class="badge badge-ghost badge-sm">Nie</span> }</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/tour-revenue-categories', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="4" class="text-center py-4">Brak kategorii</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TourRevenueCategoryListComponent {
  private service = inject(TourRevenueCategoryService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'name', sortDir: 'asc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć kategorię?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
