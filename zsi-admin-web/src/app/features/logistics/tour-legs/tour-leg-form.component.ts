import { Component, inject, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Concert, ConcertService } from '../../../core/services/concert.service';
import { TourLegService, CreateTourLegRequest } from '../../../core/services/tour-leg.service';
import { LogisticsService } from '../../../core/services/logistics.service';

@Component({
  selector: 'app-tour-leg-form',
  standalone: true,
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-2xl mx-auto">
      <h2 class="text-2xl font-bold mb-6">{{ isEdit ? 'Edytuj odcinek trasy' : 'Nowy odcinek trasy' }}</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="card bg-base-100 shadow-xl">
        <div class="card-body space-y-4">
          <div class="form-control">
            <label class="label">Trasa</label>
            <select formControlName="tourId" class="select select-bordered" [disabled]="isEdit">
              @for (t of tours; track t.id) {
                <option [value]="t.id">{{ t.name }}</option>
              }
            </select>
          </div>

          <div class="form-control">
            <label class="label">Kolejność na trasie</label>
            <input formControlName="legOrder" type="number" class="input input-bordered w-32" />
          </div>

          <div class="form-control">
            <label class="label">
              <span class="label-text">Koncert (opcjonalnie)</span>
            </label>
            <select formControlName="concertId" class="select select-bordered">
              <option value="">— Brak koncertu —</option>
              @for (c of concerts; track c.id) {
                <option [value]="c.id">
                  {{ c.name }} — {{ c.date | date:'dd.MM.yyyy HH:mm' }} ({{ c.city }})
                </option>
              }
            </select>
            @if (concerts.length === 0) {
              <label class="label">
                <span class="label-text-alt text-warning">Brak koncertów. Najpierw dodaj wydarzenie w module Koncerty.</span>
              </label>
            }
          </div>

          <div class="form-control">
            <label class="label">Miasto</label>
            <input formControlName="city" type="text" class="input input-bordered" />
          </div>

          <div class="form-control">
            <label class="label">Miejsce / venue</label>
            <input formControlName="venueName" type="text" class="input input-bordered" />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="form-control">
              <label class="label">Data odcinka</label>
              <input formControlName="legDate" type="datetime-local" class="input input-bordered" />
            </div>
            <div class="form-control">
              <label class="label">Planowany budżet</label>
              <input formControlName="plannedBudget" type="number" step="0.01" class="input input-bordered" />
            </div>
          </div>

          <div class="form-control">
            <label class="label">Waluta</label>
            <select formControlName="currency" class="select select-bordered">
              <option value="PLN">PLN</option>
              <option value="EUR">EUR</option>
              <option value="USD">USD</option>
            </select>
          </div>

          <div class="card-actions justify-end mt-4">
            <a routerLink="/admin/tour-legs" class="btn btn-ghost">Anuluj</a>
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || submitting">Zapisz</button>
          </div>
        </div>
      </form>
    </div>
  `
})
export class TourLegFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private legService = inject(TourLegService);
  private logisticsService = inject(LogisticsService);
  private concertService = inject(ConcertService);

  tours: { id: string; name: string }[] = [];
  concerts: Concert[] = [];
  isEdit = false;
  legId: string | null = null;
  submitting = false;

  form = this.fb.group({
    tourId: ['', Validators.required],
    concertId: [''],
    legOrder: [1, [Validators.required, Validators.min(1)]],
    city: ['', Validators.required],
    venueName: [''],
    legDate: [''],
    plannedBudget: [null as number | null],
    currency: ['PLN']
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!idParam && idParam !== 'new';
    this.legId = this.isEdit ? idParam : null;

    forkJoin({
      tours: this.logisticsService.getAllTours(),
      concerts: this.concertService.getAll()
    }).subscribe(({ tours, concerts }) => {
      this.tours = tours;
      this.concerts = concerts.sort(
        (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
      );

      const qpTour = this.route.snapshot.queryParamMap.get('tourId');
      if (!this.isEdit && qpTour) {
        this.form.patchValue({ tourId: qpTour });
      } else if (!this.isEdit && tours.length > 0 && !this.form.value.tourId) {
        this.form.patchValue({ tourId: tours[0].id });
      }

      if (this.isEdit && this.legId) {
        this.legService.getOne(this.legId).subscribe(leg => this.patchLegForm(leg));
      }
    });
  }

  private patchLegForm(leg: {
    tourId: string;
    concertId: string | null;
    legOrder: number;
    city: string;
    venueName: string | null;
    legDate: string | null;
    plannedBudget: number | null;
    currency: string | null;
  }) {
    this.form.patchValue({
      tourId: leg.tourId,
      concertId: leg.concertId ?? '',
      legOrder: leg.legOrder,
      city: leg.city,
      venueName: leg.venueName ?? '',
      legDate: leg.legDate ? this.toLocal(leg.legDate) : '',
      plannedBudget: leg.plannedBudget,
      currency: leg.currency ?? 'PLN'
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.submitting = true;
    const v = this.form.getRawValue();
    const req: CreateTourLegRequest = {
      tourId: v.tourId!,
      concertId: v.concertId?.trim() ? v.concertId.trim() : null,
      legOrder: v.legOrder!,
      city: v.city!,
      venueName: v.venueName?.trim() ? v.venueName : null,
      legDate: v.legDate?.trim() ? v.legDate : null,
      plannedBudget: v.plannedBudget != null && v.plannedBudget !== ('' as unknown) ? Number(v.plannedBudget) : null,
      currency: v.currency || 'PLN'
    };
    const obs$ =
      this.isEdit && this.legId ? this.legService.update(this.legId, req) : this.legService.create(req);
    obs$.subscribe({
      next: () => this.router.navigate(['/admin/tour-legs']),
      error: () => (this.submitting = false)
    });
  }

  private toLocal(iso: string) {
    const d = new Date(iso);
    d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
    return d.toISOString().slice(0, 16);
  }
}
