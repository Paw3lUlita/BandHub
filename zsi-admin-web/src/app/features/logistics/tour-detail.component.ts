import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LogisticsService, TourDetails, CreateCostRequest } from '../../core/services/logistics.service';

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    @if (tour(); as t) {
      <div class="flex flex-col gap-6">
        <div class="flex justify-between items-start">
          <div>
            <h2 class="text-3xl font-bold text-primary">{{ t.name }}</h2>
            <p class="text-gray-500">{{ t.startDate | date }} - {{ t.endDate | date }}</p>
            <p class="mt-2">{{ t.description }}</p>
          </div>
          <a routerLink="/admin/logistics" class="btn btn-ghost">← Wróć</a>
        </div>

        <div class="divider"></div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

          <div class="lg:col-span-2">
            <div class="card bg-base-100 shadow-xl">
              <div class="card-body">
                <h3 class="card-title mb-4">💰 Rejestr Kosztów</h3>

                <table class="table">
                  <thead>
                    <tr>
                      <th>Data</th>
                      <th>Tytuł</th>
                      <th class="text-right">Kwota</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (cost of t.costs; track cost.id) {
                      <tr>
                        <td>{{ cost.date | date:'shortDate' }}</td>
                        <td class="font-medium">{{ cost.title }}</td>
                        <td class="text-right font-mono text-error">
                          - {{ cost.amount | currency:cost.currency }}
                        </td>
                      </tr>
                    } @empty {
                      <tr><td colspan="3" class="text-center italic">Brak kosztów.</td></tr>
                    }
                  </tbody>
                  <tfoot>
                    <tr class="bg-base-200 font-bold text-lg">
                      <td colspan="2">SUMA WYDATKÓW:</td>
                      <td class="text-right text-error">{{ totalCost() | currency:'PLN' }}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="card bg-base-200 shadow-lg sticky top-4">
              <div class="card-body">
                <h3 class="card-title text-sm uppercase text-gray-500">Dodaj Nowy Wydatek</h3>

                <form [formGroup]="costForm" (ngSubmit)="addCost(t.id)">
                  <div class="form-control">
                    <label class="label">Tytuł</label>
                    <input formControlName="title" type="text" class="input input-sm input-bordered" placeholder="np. Hotel Berlin" />
                  </div>

                  <div class="grid grid-cols-2 gap-2 mt-2">
                    <div class="form-control">
                      <label class="label">Kwota</label>
                      <input formControlName="amount" type="number" class="input input-sm input-bordered" />
                    </div>
                    <div class="form-control">
                      <label class="label">Waluta</label>
                      <select formControlName="currency" class="select select-sm select-bordered">
                        <option value="PLN">PLN</option>
                        <option value="EUR">EUR</option>
                        <option value="USD">USD</option>
                      </select>
                    </div>
                  </div>

                  <div class="form-control mt-2">
                    <label class="label">Data</label>
                    <input formControlName="date" type="datetime-local" class="input input-sm input-bordered" />
                  </div>

                  <button type="submit" class="btn btn-primary btn-sm w-full mt-6" [disabled]="costForm.invalid">
                    + Dodaj Koszt
                  </button>
                </form>

              </div>
            </div>
          </div>

        </div>
      </div>
    } @else {
      <div class="flex justify-center p-10"><span class="loading loading-spinner loading-lg"></span></div>
    }
  `
})
export class TourDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private service = inject(LogisticsService);
  private fb = inject(FormBuilder);

  tour = signal<TourDetails | null>(null);

  costForm = this.fb.group({
    title: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['PLN', Validators.required],
    date: ['', Validators.required]
  });

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.service.getOneTour(id).subscribe(data => this.tour.set(data));
    }
  }

  addCost(tourId: string) {
    if (this.costForm.valid) {
      const request = this.costForm.value as CreateCostRequest;
      this.service.addCost(tourId, request).subscribe({
        next: () => {
          this.costForm.reset({ currency: 'PLN', amount: 0 });
          this.loadData();
        }
      });
    }
  }

  totalCost() {
    return this.tour()?.costs.reduce((sum, c) => sum + c.amount, 0) || 0;
  }
}
