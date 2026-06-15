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
    <div class="bh-filter-bar mb-4">
      <div class="flex items-center gap-2 flex-1 min-w-[200px] order-first w-full sm:w-auto sm:order-none sm:flex-[2]">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 opacity-40 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          type="text"
          class="input input-bordered input-sm flex-1 bg-base-100"
          placeholder="Szukaj..."
          [ngModel]="params().q"
          (ngModelChange)="onQueryChange($event)"
        />
      </div>

      <div class="flex items-center gap-2">
        <label class="text-xs font-semibold uppercase tracking-wide text-base-content/50">Sortuj</label>
        <select class="select select-bordered select-sm bg-base-100" [ngModel]="params().sortBy" (ngModelChange)="onSortByChange($event)">
          @for (opt of sortOptions(); track opt.value) {
            <option [value]="opt.value">{{ opt.label }}</option>
          }
        </select>
      </div>

      <div class="flex items-center gap-2">
        <select class="select select-bordered select-sm bg-base-100" [ngModel]="params().sortDir" (ngModelChange)="onSortDirChange($event)">
          <option value="asc">Rosnąco</option>
          <option value="desc">Malejąco</option>
        </select>
      </div>

      <div class="flex items-center gap-2">
        <label class="text-xs font-semibold uppercase tracking-wide text-base-content/50">Na str.</label>
        <select class="select select-bordered select-sm bg-base-100" [ngModel]="params().size" (ngModelChange)="onSizeChange($event)">
          <option [value]="5">5</option>
          <option [value]="10">10</option>
          <option [value]="20">20</option>
          <option [value]="50">50</option>
        </select>
      </div>

      @if (totalElements() >= 0) {
        <span class="text-xs font-medium text-base-content/50 hidden sm:inline">Łącznie: {{ totalElements() }}</span>
      }

      @if (totalPages() > 1) {
        <div class="flex items-center gap-1 ml-auto">
          <button class="btn btn-sm btn-ghost" [disabled]="params().page <= 0" (click)="goPage(params().page - 1)">‹</button>
          <span class="text-xs font-medium px-2">{{ params().page + 1 }} / {{ totalPages() }}</span>
          <button class="btn btn-sm btn-ghost" [disabled]="params().page >= totalPages() - 1" (click)="goPage(params().page + 1)">›</button>
        </div>
      }
    </div>
  `,
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
