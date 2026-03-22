import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';

@Component({
  selector: 'app-tour-settlement-close',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-xl mx-auto card bg-base-100 shadow-xl">
      <div class="card-body">
        <h2 class="card-title">Zamknięcie rozliczenia trasy</h2>
        <p class="text-sm opacity-70">
          Przelicza koszty, przychody ręczne i zamówienia biletów (jak funkcja SQL w bazie) i zapisuje wiersz rozliczenia.
        </p>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4 mt-4">
          <div class="form-control">
            <label class="label">Trasa</label>
            <select formControlName="tourId" class="select select-bordered">
              @for (t of tours; track t.id) {
                <option [value]="t.id">{{ t.name }}</option>
              }
            </select>
          </div>
          <div class="form-control">
            <label class="label">Notatki (opcjonalnie)</label>
            <textarea formControlName="notes" class="textarea textarea-bordered h-24"></textarea>
          </div>
          @if (errorMsg()) {
            <div class="alert alert-error text-sm">{{ errorMsg() }}</div>
          }
          <div class="card-actions justify-end">
            <a routerLink="/admin/tour-settlements" class="btn btn-ghost">Lista</a>
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || busy">Przelicz i zamknij</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class TourSettlementCloseComponent implements OnInit {
  private fb = inject(FormBuilder);
  private logistics = inject(LogisticsService);
  private router = inject(Router);

  tours: { id: string; name: string }[] = [];
  busy = false;
  errorMsg = signal<string | null>(null);

  form = this.fb.group({
    tourId: ['', Validators.required],
    notes: ['']
  });

  ngOnInit() {
    this.logistics.getAllTours().subscribe(t => {
      this.tours = t;
      if (t.length > 0 && !this.form.value.tourId) {
        this.form.patchValue({ tourId: t[0].id });
      }
    });
  }

  onSubmit() {
    const tourId = this.form.value.tourId;
    if (!tourId) return;
    this.busy = true;
    this.errorMsg.set(null);
    this.logistics.closeSettlementForTour(tourId, { notes: this.form.value.notes?.trim() || null }).subscribe({
      next: s => {
        this.busy = false;
        this.router.navigate(['/admin/tour-settlements', s.id]);
      },
      error: () => {
        this.errorMsg.set('Operacja nie powiodła się (sprawdź logi backendu).');
        this.busy = false;
      }
    });
  }
}
