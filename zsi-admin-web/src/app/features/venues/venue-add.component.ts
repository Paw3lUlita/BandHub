import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { VenueService, CreateVenueRequest } from '../../core/services/venue.service';

@Component({
  selector: 'app-venue-add',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-2xl mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">Nowe Miejsce Koncertowe</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Nazwa Miejsca (np. Klub Stodoła)</span></label>
          <input type="text" formControlName="name" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('name')" />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div class="form-control">
            <label class="label"><span class="label-text">Miasto</span></label>
            <input type="text" formControlName="city" class="input input-bordered"
                   [class.input-error]="isInvalid('city')" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Ulica</span></label>
            <input type="text" formControlName="street" class="input input-bordered"
                   [class.input-error]="isInvalid('street')" />
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
          <div class="form-control">
            <label class="label"><span class="label-text">Pojemność (osób)</span></label>
            <input type="number" formControlName="capacity" class="input input-bordered"
                   [class.input-error]="isInvalid('capacity')" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Email Kontaktowy</span></label>
            <input type="email" formControlName="contactEmail" class="input input-bordered"
                   [class.input-error]="isInvalid('contactEmail')" />
          </div>
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/venues" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            @if(isSubmitting) { <span class="loading loading-spinner"></span> }
            Zapisz
          </button>
        </div>

      </form>
    </div>
  `
})
export class VenueAddComponent {
  private fb = inject(FormBuilder);
  private venueService = inject(VenueService);
  private router = inject(Router);

  isSubmitting = false;

  form = this.fb.group({
    name: ['', Validators.required],
    city: ['', Validators.required],
    street: ['', Validators.required],
    capacity: [0, [Validators.required, Validators.min(1)]],
    contactEmail: ['', [Validators.required, Validators.email]]
  });

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isSubmitting = true;
    const request = this.form.value as CreateVenueRequest;

    this.venueService.create(request).subscribe({
      next: () => {
        this.router.navigate(['/admin/venues']);
      },
      error: (err) => {
        console.error('Błąd zapisu', err);
        this.isSubmitting = false;
      }
    });
  }
}
