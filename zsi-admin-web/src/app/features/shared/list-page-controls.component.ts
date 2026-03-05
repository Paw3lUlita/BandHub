import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface ListPageParams {
  page: number;
  size: number;
  sortBy: string;
  sortDir: string;
  q: string;
}

export interface SortOption {
  value: string;
  label: string;
}

@Component({
  selector: 'app-list-page-controls',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="flex flex-wrap items-center gap-4 mb-4 p-3 bg-base-200 rounded-lg">
      <div class="flex items-center gap-2">
        <label class="text-sm font-medium">Sortuj po:</label>
        <select class="select select-bordered select-sm" [ngModel]="params().sortBy" (ngModelChange)="onSortByChange($event)">
          @for (opt of sortOptions(); track opt.value) {
            <option [value]="opt.value">{{ opt.label }}</option>
          }
        </select>
      </div>
      <div class="flex items-center gap-2">
        <label class="text-sm font-medium">Kierunek:</label>
        <select class="select select-bordered select-sm" [ngModel]="params().sortDir" (ngModelChange)="onSortDirChange($event)">
          <option value="asc">Rosnąco</option>
          <option value="desc">Malejąco</option>
        </select>
      </div>
      <div class="flex items-center gap-2">
        <label class="text-sm font-medium">Na stronie:</label>
        <select class="select select-bordered select-sm" [ngModel]="params().size" (ngModelChange)="onSizeChange($event)">
          <option [value]="5">5</option>
          <option [value]="10">10</option>
          <option [value]="20">20</option>
          <option [value]="50">50</option>
        </select>
      </div>
      <div class="flex items-center gap-2 flex-1 min-w-[180px]">
        <label class="text-sm font-medium">Szukaj:</label>
        <input type="text" class="input input-bordered input-sm flex-1" placeholder="Filtruj..."
               [ngModel]="params().q" (ngModelChange)="onQueryChange($event)" />
      </div>
      @if (totalElements() >= 0) {
        <span class="text-sm opacity-70">Łącznie: {{ totalElements() }}</span>
      }
      @if (totalPages() > 1) {
        <div class="flex items-center gap-2 ml-auto">
          <button class="btn btn-sm btn-ghost" [disabled]="params().page <= 0" (click)="goPage(params().page - 1)">←</button>
          <span class="text-sm">Strona {{ params().page + 1 }} / {{ totalPages() }}</span>
          <button class="btn btn-sm btn-ghost" [disabled]="params().page >= totalPages() - 1" (click)="goPage(params().page + 1)">→</button>
        </div>
      }
    </div>
  `
})
export class ListPageControlsComponent {
  params = input.required<ListPageParams>();
  sortOptions = input.required<SortOption[]>();
  totalElements = input<number>(-1);
  totalPages = input<number>(0);

  paramsChange = output<ListPageParams>();

  goPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.paramsChange.emit({ ...this.params(), page });
    }
  }

  onSortByChange(sortBy: string) {
    this.paramsChange.emit({ ...this.params(), sortBy, page: 0 });
  }

  onSortDirChange(sortDir: string) {
    this.paramsChange.emit({ ...this.params(), sortDir, page: 0 });
  }

  onSizeChange(size: number) {
    this.paramsChange.emit({ ...this.params(), size: Number(size), page: 0 });
  }

  onQueryChange(q: string) {
    this.paramsChange.emit({ ...this.params(), q: q ?? '', page: 0 });
  }
}
