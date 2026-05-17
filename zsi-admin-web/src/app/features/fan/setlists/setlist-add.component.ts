import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';
import { ConcertService, Concert } from '../../../core/services/concert.service';
import { SetlistService } from '../../../core/services/setlist.service';
import { SetlistItemService } from '../../../core/services/setlist-item.service';

@Component({
  selector: 'app-setlist-add',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-4xl mx-auto">
      <h2 class="text-2xl font-bold mb-6">{{ isEditMode ? 'Edytuj Setlistę' : 'Nowa Setlista' }}</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="card bg-base-100 shadow-xl mb-6">
          <div class="card-body">
            <h3 class="card-title text-sm uppercase text-gray-400 mb-4">Koncert i publikacja</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="form-control">
                <label class="label"><span class="label-text">Koncert</span></label>
                <select formControlName="concertId" class="select select-bordered"
                        [class.select-error]="isInvalid('concertId')">
                  <option value="" disabled>Wybierz koncert...</option>
                  @for (c of concerts; track c.id) {
                    <option [value]="c.id">{{ c.name }} ({{ c.city }})</option>
                  }
                </select>
              </div>

              <div class="form-control">
                <label class="label"><span class="label-text">Tytuł setlisty</span></label>
                <input type="text" formControlName="title" class="input input-bordered"
                       [class.input-error]="isInvalid('title')" />
              </div>

              <div class="form-control md:col-span-2">
                <label class="label">
                  <span class="label-text">Data publikacji (opcjonalnie)</span>
                  <span class="label-text-alt">Puste = szkic (niewidoczne w aplikacji mobilnej)</span>
                </label>
                <input type="datetime-local" formControlName="publishedAt" class="input input-bordered" />
              </div>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl mb-6 border-l-4 border-primary">
          <div class="card-body">
            <div class="flex justify-between items-center mb-4">
              <h3 class="card-title text-sm uppercase text-gray-400">Utwory</h3>
              <button type="button" class="btn btn-sm btn-outline btn-primary" (click)="addSong()">
                + Dodaj utwór
              </button>
            </div>

            <div formArrayName="items">
              @for (row of items.controls; track $index) {
                <div [formGroupName]="$index"
                     class="flex flex-col md:flex-row gap-3 items-end mb-3 p-4 bg-base-200 rounded-box">
                  <div class="form-control w-20">
                    <label class="label text-xs">Kolejność</label>
                    <input type="number" formControlName="songOrder" class="input input-sm input-bordered" min="1" />
                  </div>
                  <div class="form-control flex-1">
                    <label class="label text-xs">Tytuł utworu</label>
                    <input type="text" formControlName="songTitle" class="input input-sm input-bordered"
                           placeholder="np. Enter Sandman" />
                  </div>
                  <div class="form-control w-28">
                    <label class="label text-xs">Czas (sek.)</label>
                    <input type="number" formControlName="durationSeconds" class="input input-sm input-bordered"
                           placeholder="opcj." min="0" />
                  </div>
                  <button type="button" class="btn btn-sm btn-square btn-ghost text-error mb-1"
                          (click)="removeSong($index)">
                    Usuń
                  </button>
                </div>
              } @empty {
                <p class="text-center py-4 text-gray-500 italic">Dodaj co najmniej jeden utwór.</p>
              }
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-4 mb-10">
          <a routerLink="/admin/setlists" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            @if (isSubmitting) { <span class="loading loading-spinner"></span> }
            {{ isEditMode ? 'Zapisz' : 'Utwórz setlistę' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class SetlistAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private setlistService = inject(SetlistService);
  private itemService = inject(SetlistItemService);
  private concertService = inject(ConcertService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  concerts: Concert[] = [];
  isEditMode = false;
  setlistId: string | null = null;
  existingItemIds: string[] = [];
  isSubmitting = false;

  form = this.fb.group({
    concertId: [{ value: '', disabled: false }, Validators.required],
    title: ['', Validators.required],
    publishedAt: [''],
    items: this.fb.array([])
  });

  get items() {
    return this.form.get('items') as FormArray;
  }

  ngOnInit() {
    this.concertService.getAll().subscribe((list) => (this.concerts = list));

    this.setlistId = this.route.snapshot.paramMap.get('id');
    if (this.setlistId) {
      this.isEditMode = true;
      this.form.get('concertId')?.disable();
      this.loadForEdit(this.setlistId);
    } else {
      this.addSong();
    }
  }

  private loadForEdit(id: string) {
    forkJoin({
      setlist: this.setlistService.getOne(id),
      items: this.setlistService.getItems(id)
    }).subscribe(({ setlist, items }) => {
      this.form.patchValue({
        concertId: setlist.concertId,
        title: setlist.title,
        publishedAt: setlist.publishedAt ? this.toDatetimeLocal(setlist.publishedAt) : ''
      });
      this.existingItemIds = items.map((i) => i.id);
      this.items.clear();
      const sorted = [...items].sort((a, b) => a.songOrder - b.songOrder);
      if (sorted.length === 0) {
        this.addSong();
      } else {
        sorted.forEach((item) => this.pushSongRow(item.songOrder, item.songTitle, item.durationSeconds));
      }
    });
  }

  addSong() {
    const nextOrder = this.items.length + 1;
    this.pushSongRow(nextOrder, '', null);
  }

  private pushSongRow(songOrder: number, songTitle: string, durationSeconds: number | null) {
    this.items.push(
      this.fb.group({
        songOrder: [songOrder, [Validators.required, Validators.min(1)]],
        songTitle: [songTitle, Validators.required],
        durationSeconds: [durationSeconds]
      })
    );
  }

  removeSong(index: number) {
    this.items.removeAt(index);
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.form.invalid || this.items.length === 0) {
      this.form.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;

    const raw = this.form.getRawValue();
    const publishedAt = raw.publishedAt ? this.toIsoDateTime(raw.publishedAt) : null;

    if (this.isEditMode && this.setlistId) {
      this.setlistService
        .update(this.setlistId, { title: raw.title!, publishedAt })
        .pipe(
          switchMap(() => {
            const deletes =
              this.existingItemIds.length > 0
                ? forkJoin(this.existingItemIds.map((id) => this.itemService.delete(id)))
                : of(null);
            return deletes;
          }),
          switchMap(() => this.createAllItems(this.setlistId!))
        )
        .subscribe({
          next: () => this.router.navigate(['/admin/setlists']),
          error: () => (this.isSubmitting = false)
        });
    } else {
      this.setlistService
        .create({
          concertId: raw.concertId!,
          title: raw.title!,
          createdBy: null,
          publishedAt
        })
        .pipe(
          switchMap((res) => {
            const id = this.parseCreatedId(res);
            if (!id) {
              throw new Error('Brak ID utworzonej setlisty');
            }
            return this.createAllItems(id);
          })
        )
        .subscribe({
          next: () => this.router.navigate(['/admin/setlists']),
          error: () => (this.isSubmitting = false)
        });
    }
  }

  private createAllItems(setlistId: string) {
    const rows = this.items.controls.map((c) => c.value);
    const requests = rows.map((row) =>
      this.itemService.create({
        setlistId,
        songTitle: row.songTitle!,
        songOrder: Number(row.songOrder),
        durationSeconds:
          row.durationSeconds != null && row.durationSeconds !== ''
            ? Number(row.durationSeconds)
            : null
      })
    );
    return requests.length ? forkJoin(requests) : of(null);
  }

  private parseCreatedId(res: { headers: { get: (n: string) => string | null } }): string | null {
    const location = res.headers.get('Location');
    if (!location) return null;
    const parts = location.split('/');
    return parts[parts.length - 1] || null;
  }

  private toDatetimeLocal(iso: string): string {
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  private toIsoDateTime(local: string): string {
    if (local.length === 16) {
      return `${local}:00`;
    }
    return local;
  }
}
