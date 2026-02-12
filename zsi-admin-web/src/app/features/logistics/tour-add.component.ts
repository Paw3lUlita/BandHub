import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LogisticsService, CreateTourRequest } from '../../core/services/logistics.service';

@Component({
  selector: 'app-tour-add',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-2xl mx-auto">
      <h2 class="text-2xl font-bold mb-6">Planowanie Nowej Trasy</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="card bg-base-100 shadow-xl">
        <div class="card-body">

          <div class="form-control">
            <label class="label">Nazwa Trasy</label>
            <input formControlName="name" type="text" class="input input-bordered" placeholder="np. Lato 2026" />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="form-control">
              <label class="label">Data Początku</label>
              <input formControlName="startDate" type="datetime-local" class="input input-bordered" />
            </div>
            <div class="form-control">
              <label class="label">Data Końca</label>
              <input formControlName="endDate" type="datetime-local" class="input input-bordered" />
            </div>
          </div>

          <div class="form-control">
            <label class="label">Opis / Notatki</label>
            <textarea formControlName="description" class="textarea textarea-bordered h-24"></textarea>
          </div>

          <div class="card-actions justify-end mt-4">
            <a routerLink="/admin/logistics" class="btn btn-ghost">Anuluj</a>
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid">
              Zapisz Trasę
            </button>
          </div>
        </div>
      </form>
    </div>
  `
})
export class TourAddComponent {
  private fb = inject(FormBuilder);
  private service = inject(LogisticsService);
  private router = inject(Router);

  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  onSubmit() {
    if (this.form.valid) {
      const request = this.form.value as CreateTourRequest;
      this.service.createTour(request).subscribe(() => {
        this.router.navigate(['/admin/logistics']);
      });
    }
  }
}
