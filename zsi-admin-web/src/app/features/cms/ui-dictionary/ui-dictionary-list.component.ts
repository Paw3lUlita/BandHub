import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, of, timeout } from 'rxjs';
import {
  CmsService,
  CreateUiDictionaryEntryRequest,
  UiDictionaryEntry
} from '../../../core/services/cms.service';

interface DraftRow {
  original: UiDictionaryEntry;
  value: string;
  description: string;
  saving: boolean;
  error: string | null;
  saved: boolean;
}

@Component({
  selector: 'app-ui-dictionary-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-5xl mx-auto">
      <h2 class="text-2xl font-bold mb-2">Słownik UI</h2>
      <p class="text-sm text-gray-500 mb-6">
        Każda etykieta, przycisk i komunikat widoczny w aplikacji mobilnej fana jest sterowany z tej tabeli.
        Klucz jest <strong>identyfikatorem w kodzie</strong> (np. <code>auth.button.login</code>),
        wartość możesz dowolnie zmieniać. Mobilka pobiera płaską mapę z
        <code>/api/public/ui-dictionary</code> przy starcie i cache'uje ją lokalnie.
      </p>

      @if (loading()) {
        <div class="flex flex-col items-center py-10 gap-3">
          <span class="loading loading-spinner loading-lg"></span>
          <span class="text-sm text-gray-500">Ładowanie słownika...</span>
        </div>
      } @else if (loadError()) {
        <div class="alert alert-error">
          <span>{{ loadError() }}</span>
          <button class="btn btn-sm btn-ghost" (click)="reload()">Spróbuj ponownie</button>
        </div>
      } @else {
        <div class="card bg-base-200 mb-6">
          <div class="card-body">
            <h3 class="card-title text-base">Dodaj nowy klucz</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
              <input
                type="text"
                class="input input-bordered input-sm"
                placeholder="np. button.save"
                [(ngModel)]="newKey"
              />
              <input
                type="text"
                class="input input-bordered input-sm"
                placeholder="Wartość (np. 'Zapisz zmiany')"
                [(ngModel)]="newValue"
              />
              <input
                type="text"
                class="input input-bordered input-sm"
                placeholder="Opis (opcjonalnie)"
                [(ngModel)]="newDescription"
              />
            </div>
            @if (createError()) {
              <div class="alert alert-error mt-2 py-2 text-sm">{{ createError() }}</div>
            }
            <div class="card-actions justify-end mt-2">
              <button
                class="btn btn-primary btn-sm"
                [disabled]="!newKey || !newValue || creating()"
                (click)="addEntry()"
              >
                @if (creating()) { <span class="loading loading-spinner loading-xs"></span> }
                Dodaj
              </button>
            </div>
          </div>
        </div>

        <div class="form-control mb-3">
          <input
            type="text"
            class="input input-bordered input-sm w-full md:max-w-sm"
            placeholder="Filtruj po kluczu lub wartości..."
            [(ngModel)]="filter"
          />
        </div>

        <div class="overflow-x-auto bg-base-100 rounded-lg shadow-sm">
          <table class="table table-zebra">
            <thead>
              <tr>
                <th class="w-1/4">Klucz</th>
                <th class="w-1/2">Wartość</th>
                <th class="w-1/4">Opis</th>
                <th class="w-32 text-right">Akcje</th>
              </tr>
            </thead>
            <tbody>
              @for (row of visibleRows(); track row.original.key) {
                <tr>
                  <td><code class="text-xs">{{ row.original.key }}</code></td>
                  <td>
                    <input
                      type="text"
                      class="input input-bordered input-sm w-full"
                      [ngModel]="row.value"
                      (ngModelChange)="onValueChange(row, $event)"
                    />
                    @if (row.error) {
                      <span class="text-error text-xs">{{ row.error }}</span>
                    }
                    @if (row.saved) {
                      <span class="text-success text-xs">Zapisano.</span>
                    }
                  </td>
                  <td>
                    <input
                      type="text"
                      class="input input-bordered input-sm w-full"
                      [ngModel]="row.description"
                      (ngModelChange)="onDescriptionChange(row, $event)"
                    />
                  </td>
                  <td class="text-right">
                    <button
                      class="btn btn-primary btn-xs mr-1"
                      [disabled]="row.saving || !isDirty(row)"
                      (click)="saveRow(row)"
                    >
                      @if (row.saving) { <span class="loading loading-spinner loading-xs"></span> }
                      Zapisz
                    </button>
                    <button
                      class="btn btn-error btn-xs"
                      [disabled]="row.saving"
                      (click)="deleteRow(row)"
                    >
                      Usuń
                    </button>
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="4" class="text-center text-sm text-gray-500 py-6">
                    Brak wpisów pasujących do filtra.
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `
})
export class UiDictionaryListComponent implements OnInit {
  private cmsService = inject(CmsService);

  loading = signal(true);
  loadError = signal<string | null>(null);
  rows = signal<DraftRow[]>([]);

  filter = '';

  newKey = '';
  newValue = '';
  newDescription = '';
  creating = signal(false);
  createError = signal<string | null>(null);

  // Filtrowanie liczone na fly - male zbiory wpisow, nie warto budowac indeksow.
  visibleRows = computed(() => {
    const q = this.filter.trim().toLowerCase();
    const rows = this.rows();
    if (!q) {
      return rows;
    }
    return rows.filter(
      (row) =>
        row.original.key.toLowerCase().includes(q) ||
        row.value.toLowerCase().includes(q) ||
        (row.description ?? '').toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.loadError.set(null);

    this.cmsService
      .getUiDictionary()
      .pipe(
        timeout(15000),
        catchError((err) => {
          console.error('[ui-dictionary] load error', err);
          if (err?.name === 'TimeoutError') {
            this.loadError.set('Backend nie odpowiedzial w 15s.');
          } else if (err?.status === 0) {
            this.loadError.set('Brak polaczenia z backendem (status 0 / CORS).');
          } else {
            this.loadError.set(
              err?.error?.message ?? `Nie udalo sie pobrac slownika (status ${err?.status ?? '?'}).`
            );
          }
          return of([] as UiDictionaryEntry[]);
        })
      )
      .subscribe((entries) => {
        const sorted = [...entries].sort((a, b) => a.key.localeCompare(b.key));
        this.rows.set(sorted.map((entry) => this.toDraft(entry)));
        this.loading.set(false);
      });
  }

  isDirty(row: DraftRow): boolean {
    return (
      row.value !== row.original.value ||
      (row.description ?? '') !== (row.original.description ?? '')
    );
  }

  onValueChange(row: DraftRow, value: string): void {
    this.rows.update((rows) =>
      rows.map((r) =>
        r.original.key === row.original.key ? { ...r, value, error: null, saved: false } : r
      )
    );
  }

  onDescriptionChange(row: DraftRow, description: string): void {
    this.rows.update((rows) =>
      rows.map((r) =>
        r.original.key === row.original.key
          ? { ...r, description, error: null, saved: false }
          : r
      )
    );
  }

  saveRow(row: DraftRow): void {
    this.rows.update((rows) =>
      rows.map((r) =>
        r.original.key === row.original.key ? { ...r, saving: true, error: null, saved: false } : r
      )
    );

    this.cmsService
      .updateUiDictionaryEntry(row.original.key, {
        value: row.value,
        description: row.description?.trim() ? row.description : null
      })
      .subscribe({
        next: (updated) => {
          this.rows.update((rows) =>
            rows.map((r) =>
              r.original.key === updated.key
                ? {
                    ...this.toDraft(updated),
                    saved: true
                  }
                : r
            )
          );
        },
        error: (err) => {
          console.error('[ui-dictionary] save error', err);
          this.rows.update((rows) =>
            rows.map((r) =>
              r.original.key === row.original.key
                ? {
                    ...r,
                    saving: false,
                    error: err?.error?.message ?? 'Blad zapisu wpisu.'
                  }
                : r
            )
          );
        }
      });
  }

  deleteRow(row: DraftRow): void {
    if (!confirm(`Usunac klucz '${row.original.key}'? Mobilka straci tlumaczenie.`)) {
      return;
    }
    this.rows.update((rows) =>
      rows.map((r) =>
        r.original.key === row.original.key ? { ...r, saving: true, error: null } : r
      )
    );
    this.cmsService.deleteUiDictionaryEntry(row.original.key).subscribe({
      next: () => {
        this.rows.update((rows) => rows.filter((r) => r.original.key !== row.original.key));
      },
      error: (err) => {
        console.error('[ui-dictionary] delete error', err);
        this.rows.update((rows) =>
          rows.map((r) =>
            r.original.key === row.original.key
              ? {
                  ...r,
                  saving: false,
                  error: err?.error?.message ?? 'Blad usuwania wpisu.'
                }
              : r
          )
        );
      }
    });
  }

  addEntry(): void {
    const key = this.newKey.trim();
    const value = this.newValue.trim();
    if (!key || !value) {
      return;
    }
    const description = this.newDescription.trim() || null;

    this.creating.set(true);
    this.createError.set(null);

    const request: CreateUiDictionaryEntryRequest = { key, value, description };
    this.cmsService.createUiDictionaryEntry(request).subscribe({
      next: (created) => {
        this.rows.update((rows) =>
          [...rows, this.toDraft(created)].sort((a, b) =>
            a.original.key.localeCompare(b.original.key)
          )
        );
        this.newKey = '';
        this.newValue = '';
        this.newDescription = '';
        this.creating.set(false);
      },
      error: (err) => {
        console.error('[ui-dictionary] create error', err);
        this.createError.set(err?.error?.message ?? 'Blad dodawania klucza.');
        this.creating.set(false);
      }
    });
  }

  private toDraft(entry: UiDictionaryEntry): DraftRow {
    return {
      original: entry,
      value: entry.value,
      description: entry.description ?? '',
      saving: false,
      error: null,
      saved: false
    };
  }
}
