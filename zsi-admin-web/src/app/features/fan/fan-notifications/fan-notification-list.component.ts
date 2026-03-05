import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { FanNotificationService } from '../../../core/services/fan-notification.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt', label: 'Data' },
  { value: 'title', label: 'Tytuł' },
  { value: 'fanId', label: 'Fan' },
  { value: 'module', label: 'Moduł' }
];

@Component({
  selector: 'app-fan-notification-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Powiadomienia Fanów</h2>
        <a routerLink="/admin/fan-notifications/new" class="btn btn-primary btn-sm">+ Dodaj</a>
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
            <th>Tytuł</th>
            <th>Fan</th>
            <th>Broadcast</th>
            <th>Moduł</th>
            <th>Data</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (item of (pageData$ | async)?.content ?? []; track item.id) {
            <tr class="hover">
              <td class="font-bold">{{ item.title }}</td>
              <td class="text-xs">{{ item.fanId }}</td>
              <td>@if (item.broadcast) { <span class="badge badge-primary badge-sm">Tak</span> } @else { <span class="badge badge-ghost badge-sm">Nie</span> }</td>
              <td>{{ item.module || '-' }}</td>
              <td class="text-sm">{{ item.createdAt | date:'short' }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/fan-notifications', item.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(item.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="6" class="text-center py-4">Brak powiadomień</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class FanNotificationListComponent {
  private service = inject(FanNotificationService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.service.getPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir, q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Usunąć powiadomienie?')) {
      this.service.delete(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
