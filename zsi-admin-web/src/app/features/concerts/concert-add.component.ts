import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ConcertService, CreateConcertRequest } from '../../core/services/concert.service';
import { VenueService, Venue } from '../../core/services/venue.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-concert-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-4xl mx-auto">
      <h2 class="text-2xl font-bold mb-6">Planowanie Koncertu</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">

        <div class="card bg-base-100 shadow-xl mb-6">
          <div class="card-body">
            <h3 class="card-title text-sm uppercase text-gray-400 mb-4">Informacje Podstawowe</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="form-control">
                <label class="label"><span class="label-text">Nazwa Wydarzenia</span></label>
                <input type="text" formControlName="name" class="input input-bordered"
                       [class.input-error]="isInvalid('name')" />
              </div>

              <div class="form-control">
                <label class="label"><span class="label-text">Data i Godzina</span></label>
                <input type="datetime-local" formControlName="date" class="input input-bordered"
                       [class.input-error]="isInvalid('date')" />
              </div>

              <div class="form-control">
                <label class="label"><span class="label-text">Miejsce (Klub/Hala)</span></label>
                <select formControlName="venueId" class="select select-bordered"
                        [class.select-error]="isInvalid('venueId')">
                  <option value="" disabled selected>Wybierz miejsce...</option>
                  @for (venue of venues$ | async; track venue.id) {
                    <option [value]="venue.id">{{ venue.name }} ({{ venue.city }})</option>
                  }
                </select>
              </div>

              <div class="form-control">
                <label class="label"><span class="label-text">Link do plakatu (URL)</span></label>
                <input type="text" formControlName="imageUrl" class="input input-bordered" />
              </div>
            </div>

            <div class="form-control mt-4">
              <label class="label"><span class="label-text">Opis wydarzenia</span></label>
              <textarea formControlName="description" class="textarea textarea-bordered h-24"></textarea>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl mb-6 border-l-4 border-primary">
          <div class="card-body">
            <div class="flex justify-between items-center mb-4">
              <h3 class="card-title text-sm uppercase text-gray-400">Bilety</h3>
              <button type="button" class="btn btn-sm btn-outline btn-primary" (click)="addTicketPool()">
                + Dodaj Pulę
              </button>
            </div>

            <div formArrayName="ticketPools">
              @for (pool of ticketPools.controls; track $index) {
                <div [formGroupName]="$index" class="flex flex-col md:flex-row gap-4 items-end mb-4 p-4 bg-base-200 rounded-box relative group">

                  <div class="form-control flex-1">
                    <label class="label text-xs">Nazwa Puli</label>
                    <input type="text" formControlName="name" placeholder="np. Płyta" class="input input-sm input-bordered" />
                  </div>

                  <div class="form-control w-24">
                    <label class="label text-xs">Cena</label>
                    <input type="number" formControlName="price" class="input input-sm input-bordered" />
                  </div>

                  <div class="form-control w-20">
                    <label class="label text-xs">Waluta</label>
                    <input type="text" formControlName="currency" class="input input-sm input-bordered" readonly />
                  </div>

                  <div class="form-control w-24">
                    <label class="label text-xs">Ilość</label>
                    <input type="number" formControlName="totalQuantity" class="input input-sm input-bordered" />
                  </div>

                  <button type="button" class="btn btn-sm btn-square btn-ghost text-error" (click)="removeTicketPool($index)">
                    🗑️
                  </button>
                </div>
              } @empty {
                <div class="text-center py-4 text-gray-500 italic">
                  Brak zdefiniowanych biletów. Kliknij "Dodaj Pulę".
                </div>
              }
            </div>

            @if (ticketPools.length === 0 && form.touched) {
              <div class="text-error text-sm mt-2">Musisz dodać przynajmniej jedną pulę biletów.</div>
            }
          </div>
        </div>

        <div class="flex justify-end gap-4 mb-10">
          <a routerLink="/admin/concerts" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary btn-wide" [disabled]="form.invalid || isSubmitting">
            @if(isSubmitting) { <span class="loading loading-spinner"></span> }
            Zaplanuj Koncert
          </button>
        </div>

      </form>
    </div>
  `
})
export class ConcertAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private concertService = inject(ConcertService);
  private venueService = inject(VenueService);
  private router = inject(Router);

  venues$!: Observable<Venue[]>;
  isSubmitting = false;

  form = this.fb.group({
    name: ['', Validators.required],
    date: ['', Validators.required],
    description: ['', Validators.required],
    imageUrl: [''],
    venueId: ['', Validators.required],
    ticketPools: this.fb.array([])
  });

  ngOnInit() {
    this.venues$ = this.venueService.getAll();
    // Dodaj domyślnie jedną pustą pulę dla wygody
    this.addTicketPool();
  }

  // Getter ułatwiający dostęp do FormArray w HTML
  get ticketPools() {
    return this.form.get('ticketPools') as FormArray;
  }

  // Metoda dodająca nowy wiersz
  addTicketPool() {
    const poolGroup = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0.1)]],
      currency: ['PLN', Validators.required],
      totalQuantity: [100, [Validators.required, Validators.min(1)]]
    });

    this.ticketPools.push(poolGroup);
  }

  // Metoda usuwająca wiersz
  removeTicketPool(index: number) {
    this.ticketPools.removeAt(index);
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // Podświetl wszystkie błędy
      return;
    }

    this.isSubmitting = true;
    const request = this.form.value as CreateConcertRequest;

    this.concertService.create(request).subscribe({
      next: () => {
        this.router.navigate(['/admin/concerts']);
      },
      error: (err) => {
        console.error('Błąd tworzenia koncertu', err);
        this.isSubmitting = false;
      }
    });
  }
}
