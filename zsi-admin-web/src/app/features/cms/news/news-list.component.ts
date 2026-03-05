import { Component, inject, signal } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { CmsService } from '../../../core/services/cms.service';
import { ListPageControlsComponent, ListPageParams, SortOption } from '../../shared/list-page-controls.component';

const SORT_OPTIONS: SortOption[] = [
  { value: 'publishedDate', label: 'Data publikacji' },
  { value: 'title', label: 'Tytuł' },
  { value: 'authorId', label: 'Autor' }
];

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink, ListPageControlsComponent],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Aktualności</h2>
        <a routerLink="/admin/news/new" class="btn btn-primary btn-sm">
          + Dodaj Artykuł
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
            <th>Obraz</th>
            <th>Tytuł</th>
            <th>Data Publikacji</th>
            <th>Autor</th>
            <th class="text-right">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (news of (pageData$ | async)?.content ?? []; track news.id) {
            <tr class="hover">
              <td>
                @if (news.imageUrl) {
                  <div class="avatar">
                    <div class="mask mask-squircle w-12 h-12">
                      <img [src]="news.imageUrl" alt="Thumbnail" />
                    </div>
                  </div>
                } @else {
                  <div class="w-12 h-12 bg-base-200 rounded-lg flex items-center justify-center text-xs text-gray-400">
                    Brak
                  </div>
                }
              </td>

              <td>
                <div class="font-bold">{{ news.title }}</div>
                <div class="text-xs opacity-50 truncate w-48">
                  {{ news.content.substring(0, 50) }}...
                </div>
              </td>

              <td>
                <div>{{ news.publishedDate | date:'shortDate' }}</div>
                <div class="text-xs opacity-50">{{ news.publishedDate | date:'shortTime' }}</div>
              </td>

              <td>
                <span class="badge badge-ghost badge-sm">{{ news.authorId }}</span>
              </td>

              <td class="text-right">
                <a [routerLink]="['/admin/news', news.id]" class="btn btn-ghost btn-xs mr-2">
                  Edytuj
                </a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(news.id)">
                  Usuń
                </button>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="text-center py-8 text-gray-500">
                Brak opublikowanych aktualności.
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class NewsListComponent {
  private cmsService = inject(CmsService);
  sortOptions = SORT_OPTIONS;

  params = signal<ListPageParams>({ page: 0, size: 10, sortBy: 'publishedDate', sortDir: 'desc', q: '' });
  pageData$ = toObservable(this.params).pipe(
    switchMap(p => this.cmsService.getNewsPage({ page: p.page, size: p.size, sortBy: p.sortBy, sortDir: p.sortDir as 'asc' | 'desc', q: p.q }))
  );

  onParamsChange(p: ListPageParams) {
    this.params.set(p);
  }

  onDelete(id: string) {
    if (confirm('Czy na pewno chcesz usunąć ten artykuł?')) {
      this.cmsService.deleteNews(id).subscribe(() => {
        this.params.update(prev => ({ ...prev }));
      });
    }
  }
}
