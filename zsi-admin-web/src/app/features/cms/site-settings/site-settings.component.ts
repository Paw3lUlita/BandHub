import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, of, timeout } from 'rxjs';
import { CmsService, GalleryImage, SiteSettings } from '../../../core/services/cms.service';
import { ApiUrlPipe } from '../../shared/api-url.pipe';

@Component({
  selector: 'app-site-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ApiUrlPipe],
  template: `
    <div class="max-w-3xl mx-auto">
      <h2 class="text-2xl font-bold mb-2">Ustawienia strony</h2>
      <p class="text-sm text-gray-500 mb-6">
        Branding i tresci, ktore widzi fan w aplikacji mobilnej (Home / About / hero image).
        Zadne z tych pol nie jest hardcodowane w kliencie - mobilka pobiera je z
        <code>/api/public/site-settings</code>.
      </p>

      @if (loading()) {
        <div class="flex flex-col items-center py-10 gap-3">
          <span class="loading loading-spinner loading-lg"></span>
          <span class="text-sm text-gray-500">Ladowanie ustawien...</span>
        </div>
      } @else if (loadError()) {
        <div class="alert alert-error">
          <span>{{ loadError() }}</span>
          <button class="btn btn-sm btn-ghost" (click)="reload()">Sprobuj ponownie</button>
        </div>
      } @else {
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Nazwa zespolu / aplikacji</span></label>
            <input type="text" formControlName="bandName" class="input input-bordered"
                   [class.input-error]="isInvalid('bandName')" />
            @if (isInvalid('bandName')) {
              <span class="text-error text-xs mt-1">Nazwa jest wymagana.</span>
            }
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Tagline (krotkie haslo)</span></label>
            <input type="text" formControlName="tagline" class="input input-bordered"
                   placeholder="np. Twoje miejsce do koncertow i merchu" />
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Hero image (URL z galerii)</span></label>
            <select formControlName="heroImageUrl" class="select select-bordered">
              <option [ngValue]="null">(brak)</option>
              @for (img of galleryImages(); track img.id) {
                <option [ngValue]="img.imageUrl">{{ img.title || img.imageUrl }}</option>
              }
            </select>
            @if (form.get('heroImageUrl')?.value) {
              <div class="mt-2">
                <img [src]="form.get('heroImageUrl')?.value | apiUrl" class="h-40 rounded-lg shadow-sm" alt="Hero preview">
              </div>
            }
          </div>

          <div class="form-control w-full mb-6">
            <label class="label"><span class="label-text">O zespole / About</span></label>
            <textarea formControlName="aboutText" class="textarea textarea-bordered h-40"></textarea>
          </div>

          <div class="flex items-center justify-between">
            @if (settings(); as s) {
              <span class="text-xs text-gray-500">
                Ostatnia aktualizacja: {{ s.updatedAt | date: 'medium' }}
                @if (s.updatedBy) { przez {{ s.updatedBy }} }
              </span>
            } @else {
              <span></span>
            }
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || saving()">
              @if (saving()) { <span class="loading loading-spinner"></span> }
              Zapisz
            </button>
          </div>

          @if (success()) {
            <div class="alert alert-success mt-4">Zapisano ustawienia.</div>
          }
          @if (saveError()) {
            <div class="alert alert-error mt-4">{{ saveError() }}</div>
          }
        </form>
      }
    </div>
  `
})
export class SiteSettingsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cmsService = inject(CmsService);

  // Signals zamiast plain props - re-render odpalany jest niezaleznie od strategy/Zone'a.
  loading = signal(true);
  saving = signal(false);
  success = signal(false);
  loadError = signal<string | null>(null);
  saveError = signal<string | null>(null);
  settings = signal<SiteSettings | null>(null);
  galleryImages = signal<GalleryImage[]>([]);

  form = this.fb.group({
    bandName: ['', [Validators.required, Validators.maxLength(255)]],
    tagline: [null as string | null, [Validators.maxLength(500)]],
    heroImageUrl: [null as string | null, [Validators.maxLength(500)]],
    aboutText: [null as string | null]
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    console.log('[site-settings] reload() start');
    this.loading.set(true);
    this.loadError.set(null);
    const startedAt = performance.now();

    this.cmsService
      .getSiteSettings()
      .pipe(timeout(15000))
      .subscribe({
        next: (settings) => {
          const elapsed = Math.round(performance.now() - startedAt);
          console.log(`[site-settings] settings zaladowane w ${elapsed}ms`, settings);
          this.settings.set(settings);
          this.form.patchValue({
            bandName: settings.bandName,
            tagline: settings.tagline ?? null,
            heroImageUrl: settings.heroImageUrl ?? null,
            aboutText: settings.aboutText ?? null
          });
          this.loading.set(false);
        },
        error: (err) => {
          console.error('[site-settings] load error', err);
          if (err?.name === 'TimeoutError') {
            this.loadError.set('Backend nie odpowiedzial w 15s. Sprawdz logi /api/admin/site-settings.');
          } else if (err?.status === 0) {
            this.loadError.set('Brak polaczenia z backendem (status 0 / CORS). Sprawdz Network tab.');
          } else {
            this.loadError.set(
              err?.error?.message ?? `Nie udalo sie pobrac ustawien strony (status ${err?.status ?? '?'}).`
            );
          }
          this.loading.set(false);
        }
      });

    // Galeria laduje sie niezaleznie - nie blokuje formularza.
    this.cmsService
      .getImages()
      .pipe(
        timeout(15000),
        catchError((err) => {
          console.warn('[site-settings] galeria nieosiagalna - zostaje bez opcji hero.', err);
          return of([] as GalleryImage[]);
        })
      )
      .subscribe((images) => {
        this.galleryImages.set(images);
      });
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    this.success.set(false);
    this.saveError.set(null);

    this.cmsService
      .updateSiteSettings({
        bandName: this.form.value.bandName!,
        tagline: this.form.value.tagline ?? null,
        heroImageUrl: this.form.value.heroImageUrl ?? null,
        aboutText: this.form.value.aboutText ?? null
      })
      .subscribe({
        next: (settings) => {
          this.settings.set(settings);
          this.success.set(true);
          this.saving.set(false);
        },
        error: (err) => {
          console.error('[site-settings] save error', err);
          this.saveError.set(err?.error?.message ?? 'Blad zapisu ustawien.');
          this.saving.set(false);
        }
      });
  }
}
