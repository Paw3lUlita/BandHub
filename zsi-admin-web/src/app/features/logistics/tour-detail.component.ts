import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import {
  CreateCostRequest,
  CreateRevenueRequest,
  LogisticsService,
  TourDetails,
  TourProfitability,
  TourSettlement,
  UpdateCostRequest,
  UpdateRevenueRequest,
  UpdateTourRequest
} from '../../core/services/logistics.service';
import { TourCostCategoryService, TourCostCategory } from '../../core/services/tour-cost-category.service';
import { TourRevenueCategoryService, TourRevenueCategory } from '../../core/services/tour-revenue-category.service';
import { TourLeg, TourLegService } from '../../core/services/tour-leg.service';

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    @if (loadError()) {
      <div class="max-w-lg mx-auto space-y-4 p-6">
        <div class="alert alert-error">{{ loadError() }}</div>
        <a routerLink="/admin/logistics" class="btn btn-ghost">Wroc do listy tras</a>
      </div>
    } @else if (tour(); as t) {
      <div class="flex flex-col gap-6">
        <div class="flex flex-wrap justify-between items-start gap-4">
          <h2 class="text-3xl font-bold text-primary">Panel Trasy</h2>
          <div class="flex flex-wrap gap-2">
            <a routerLink="/admin/logistics" class="btn btn-ghost">Powrot</a>
            <a [routerLink]="['/admin/tour-legs/new']" [queryParams]="{ tourId: t.id }" class="btn btn-outline btn-sm">+ Odcinek trasy</a>
            <button class="btn btn-error btn-outline" (click)="deleteTour(t.id)">Usun trase</button>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl">
          <div class="card-body">
            <h3 class="card-title mb-2">Dane trasy</h3>
            <form [formGroup]="tourForm" (ngSubmit)="updateTour(t.id)">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="form-control md:col-span-2">
                  <label class="label">Nazwa trasy</label>
                  <input formControlName="name" type="text" class="input input-bordered" />
                </div>
                <div class="form-control">
                  <label class="label">Data startu</label>
                  <input formControlName="startDate" type="datetime-local" class="input input-bordered" />
                </div>
                <div class="form-control">
                  <label class="label">Data konca</label>
                  <input formControlName="endDate" type="datetime-local" class="input input-bordered" />
                </div>
                <div class="form-control md:col-span-2">
                  <label class="label">Opis</label>
                  <textarea formControlName="description" class="textarea textarea-bordered h-24"></textarea>
                </div>
              </div>
              <div class="mt-4 flex justify-end">
                <button type="submit" class="btn btn-primary" [disabled]="tourForm.invalid">Zapisz zmiany</button>
              </div>
            </form>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

          <div class="lg:col-span-2">
            <div class="card bg-base-100 shadow-xl">
              <div class="card-body">
                <h3 class="card-title mb-4">Rejestr kosztow</h3>

                <table class="table">
                  <thead>
                    <tr>
                      <th>Data</th>
                      <th>Tytuł</th>
                      <th>Kategoria</th>
                      <th>Odcinek</th>
                      <th class="text-right">Kwota</th>
                      <th class="text-right">Akcje</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (cost of t.costs; track cost.id) {
                      <tr>
                        <td>{{ cost.date | date:'shortDate' }}</td>
                        <td class="font-medium">{{ cost.title }}</td>
                        <td class="text-sm">{{ cost.costCategoryName || '—' }}</td>
                        <td class="text-xs font-mono">{{ cost.tourLegId ? (cost.tourLegId | slice:0:8) + '…' : '—' }}</td>
                        <td class="text-right font-mono text-error">
                          - {{ cost.amount | currency:cost.currency }}
                        </td>
                        <td class="text-right">
                          <button type="button" class="btn btn-ghost btn-xs text-primary" (click)="editCost(cost.id)">Edytuj</button>
                          <button type="button" class="btn btn-ghost btn-xs text-error" (click)="deleteCost(t.id, cost.id)">Usun</button>
                        </td>
                      </tr>
                    } @empty {
                      <tr><td colspan="6" class="text-center italic">Brak kosztow.</td></tr>
                    }
                  </tbody>
                  <tfoot>
                    <tr class="bg-base-200 font-bold text-lg">
                      <td colspan="4">SUMA WYDATKOW:</td>
                      <td class="text-right text-error">{{ totalCost() | currency:'PLN' }}</td>
                      <td></td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>

            <div class="card bg-base-100 shadow-xl mt-6">
              <div class="card-body">
                <h3 class="card-title mb-4">Rejestr przychodow recznych</h3>

                <table class="table">
                  <thead>
                    <tr>
                      <th>Data</th>
                      <th>Tytul</th>
                      <th>Kategoria</th>
                      <th>Odcinek</th>
                      <th class="text-right">Kwota</th>
                      <th class="text-right">Akcje</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (revenue of t.revenues; track revenue.id) {
                      <tr>
                        <td>{{ revenue.date | date:'shortDate' }}</td>
                        <td class="font-medium">{{ revenue.title }}</td>
                        <td class="text-sm">{{ revenue.revenueCategoryName || '—' }}</td>
                        <td class="text-xs font-mono">{{ revenue.tourLegId ? (revenue.tourLegId | slice:0:8) + '…' : '—' }}</td>
                        <td class="text-right font-mono text-success">
                          + {{ revenue.amount | currency:revenue.currency }}
                        </td>
                        <td class="text-right">
                          <button type="button" class="btn btn-ghost btn-xs text-primary" (click)="editRevenue(revenue.id)">Edytuj</button>
                          <button type="button" class="btn btn-ghost btn-xs text-error" (click)="deleteRevenue(t.id, revenue.id)">Usun</button>
                        </td>
                      </tr>
                    } @empty {
                      <tr><td colspan="6" class="text-center italic">Brak przychodow recznych.</td></tr>
                    }
                  </tbody>
                  <tfoot>
                    <tr class="bg-base-200 font-bold text-lg">
                      <td colspan="4">SUMA PRZYCHODOW RECZNYCH:</td>
                      <td class="text-right text-success">{{ totalManualRevenue() | currency:'PLN' }}</td>
                      <td></td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="card bg-base-200 shadow-lg sticky top-4">
              <div class="card-body">
                <h3 class="card-title text-sm uppercase text-gray-500">
                  @if (editingCostId()) { Edytuj koszt } @else { Dodaj nowy wydatek }
                </h3>

                <form [formGroup]="costForm" (ngSubmit)="addCost(t.id)">
                  <div class="form-control">
                    <label class="label">Tytul</label>
                    <input formControlName="title" type="text" class="input input-sm input-bordered" placeholder="np. Hotel Berlin" />
                  </div>

                  <div class="form-control mt-2">
                    <label class="label">Kategoria kosztu</label>
                    <select formControlName="costCategoryId" class="select select-sm select-bordered">
                      <option value="">— brak —</option>
                      @for (c of costCategories(); track c.id) {
                        <option [value]="c.id">{{ c.name }}</option>
                      }
                    </select>
                  </div>

                  <div class="form-control mt-2">
                    <label class="label">Odcinek (opcjonalnie)</label>
                    <select formControlName="tourLegId" class="select select-sm select-bordered">
                      <option value="">— brak —</option>
                      @for (leg of legsForTour(); track leg.id) {
                        <option [value]="leg.id">{{ leg.legOrder }}. {{ leg.city }}</option>
                      }
                    </select>
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

                  <div class="mt-6 flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm flex-1" [disabled]="costForm.invalid">
                      @if (editingCostId()) { Zapisz } @else { Dodaj koszt }
                    </button>
                    @if (editingCostId()) {
                      <button type="button" class="btn btn-ghost btn-sm" (click)="cancelCostEdit()">Anuluj</button>
                    }
                  </div>
                </form>

                <div class="divider my-4"></div>

                <h3 class="card-title text-sm uppercase text-gray-500">
                  @if (editingRevenueId()) { Edytuj przychod } @else { Dodaj przychod reczny }
                </h3>

                <form [formGroup]="revenueForm" (ngSubmit)="addRevenue(t.id)">
                  <div class="form-control">
                    <label class="label">Tytul</label>
                    <input formControlName="title" type="text" class="input input-sm input-bordered" placeholder="np. Gaza od organizatora" />
                  </div>

                  <div class="form-control mt-2">
                    <label class="label">Kategoria przychodu</label>
                    <select formControlName="revenueCategoryId" class="select select-sm select-bordered">
                      <option value="">— brak —</option>
                      @for (c of revenueCategories(); track c.id) {
                        <option [value]="c.id">{{ c.name }}</option>
                      }
                    </select>
                  </div>

                  <div class="form-control mt-2">
                    <label class="label">Odcinek (opcjonalnie)</label>
                    <select formControlName="tourLegId" class="select select-sm select-bordered">
                      <option value="">— brak —</option>
                      @for (leg of legsForTour(); track leg.id) {
                        <option [value]="leg.id">{{ leg.legOrder }}. {{ leg.city }}</option>
                      }
                    </select>
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

                  <div class="mt-6 flex gap-2">
                    <button type="submit" class="btn btn-success btn-sm flex-1" [disabled]="revenueForm.invalid">
                      @if (editingRevenueId()) { Zapisz } @else { Dodaj przychod }
                    </button>
                    @if (editingRevenueId()) {
                      <button type="button" class="btn btn-ghost btn-sm" (click)="cancelRevenueEdit()">Anuluj</button>
                    }
                  </div>
                </form>

                <div class="divider my-4"></div>

                @if (profitability(); as p) {
                  <div class="space-y-2">
                    <h4 class="font-semibold">Rentownosc trasy</h4>
                    <div class="flex justify-between text-sm">
                      <span>Przychod z biletow:</span>
                      <span class="font-semibold text-success">{{ p.ticketRevenue | currency:p.currency }}</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span>Przychody reczne:</span>
                      <span class="font-semibold text-success">{{ p.manualRevenue | currency:p.currency }}</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span>Przychod laczny:</span>
                      <span class="font-semibold text-success">{{ p.totalRevenue | currency:p.currency }}</span>
                    </div>
                    <div class="flex justify-between text-sm">
                      <span>Koszty:</span>
                      <span class="font-semibold text-error">{{ p.totalCosts | currency:p.currency }}</span>
                    </div>
                    <div class="flex justify-between text-base pt-2 border-t border-base-300">
                      <span>Bilans:</span>
                      <span class="font-bold" [class.text-success]="p.balance >= 0" [class.text-error]="p.balance < 0">
                        {{ p.balance | currency:p.currency }}
                      </span>
                    </div>
                  </div>
                }

                <div class="divider my-4"></div>

                <h4 class="font-semibold text-sm">Rozliczenie końcowe</h4>
                @if (settlement(); as st) {
                  <p class="text-xs mt-2">Ostatnie: {{ st.settledAt | date:'short' }} — saldo {{ st.balance | currency:(st.currency ?? 'PLN') }}</p>
                  <a [routerLink]="['/admin/tour-settlements', st.id]" class="btn btn-ghost btn-xs mt-2">Szczegóły rozliczenia</a>
                } @else {
                  <p class="text-xs opacity-70 mt-2">Brak zapisanego rozliczenia.</p>
                }
                <textarea
                  class="textarea textarea-bordered textarea-sm w-full mt-2"
                  rows="2"
                  placeholder="Notatka do rozliczenia (opcjonalnie)"
                  [value]="settlementCloseNotes()"
                  (input)="settlementCloseNotes.set($any($event.target).value)"
                ></textarea>
                <button
                  type="button"
                  class="btn btn-secondary btn-sm w-full mt-2"
                  [disabled]="settlementClosing()"
                  (click)="closeSettlement(t.id)"
                >
                  Przelicz i zamknij rozliczenie
                </button>
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
  private router = inject(Router);
  private service = inject(LogisticsService);
  private costCatService = inject(TourCostCategoryService);
  private revCatService = inject(TourRevenueCategoryService);
  private legService = inject(TourLegService);
  private fb = inject(FormBuilder);

  tour = signal<TourDetails | null>(null);
  loadError = signal<string | null>(null);
  profitability = signal<TourProfitability | null>(null);
  settlement = signal<TourSettlement | null>(null);
  costCategories = signal<TourCostCategory[]>([]);
  revenueCategories = signal<TourRevenueCategory[]>([]);
  legsForTour = signal<TourLeg[]>([]);
  editingCostId = signal<string | null>(null);
  editingRevenueId = signal<string | null>(null);
  settlementCloseNotes = signal('');
  settlementClosing = signal(false);

  tourForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  costForm = this.fb.group({
    title: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['PLN', Validators.required],
    date: ['', Validators.required],
    costCategoryId: [''],
    tourLegId: ['']
  });

  revenueForm = this.fb.group({
    title: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['PLN', Validators.required],
    date: ['', Validators.required],
    revenueCategoryId: [''],
    tourLegId: ['']
  });

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }
    this.loadError.set(null);
    forkJoin({
      tour: this.service.getOneTour(id),
      profit: this.service.getProfitability(id),
      settlement: this.service.getSettlementForTour(id),
      costCats: this.costCatService.getAll(),
      revCats: this.revCatService.getAll(),
      legs: this.legService.getAll()
    }).subscribe({
      next: ({ tour, profit, settlement, costCats, revCats, legs }) => {
        this.tour.set(tour);
        this.profitability.set(profit);
        this.settlement.set(settlement);
        this.costCategories.set(costCats.filter(c => c.active));
        this.revenueCategories.set(revCats.filter(c => c.active));
        this.legsForTour.set(legs.filter(l => l.tourId === id).sort((a, b) => a.legOrder - b.legOrder));
        this.tourForm.patchValue({
          name: tour.name,
          description: tour.description,
          startDate: this.toDateTimeLocal(tour.startDate),
          endDate: this.toDateTimeLocal(tour.endDate)
        });
      },
      error: () =>
        this.loadError.set('Nie udalo sie wczytac panelu trasy (blad serwera, np. 500). Sprawdz logi backendu.')
    });
  }

  closeSettlement(tourId: string) {
    this.settlementClosing.set(true);
    this.service
      .closeSettlementForTour(tourId, { notes: this.settlementCloseNotes().trim() || null })
      .subscribe({
        next: s => {
          this.settlement.set(s);
          this.settlementClosing.set(false);
          this.settlementCloseNotes.set('');
          this.loadData();
        },
        error: () => this.settlementClosing.set(false)
      });
  }

  updateTour(tourId: string) {
    if (this.tourForm.invalid) {
      return;
    }

    const request = this.tourForm.value as UpdateTourRequest;
    this.service.updateTour(tourId, request).subscribe(() => {
      this.loadData();
    });
  }

  deleteTour(tourId: string) {
    if (!confirm('Czy na pewno chcesz usunac trase?')) {
      return;
    }

    this.service.deleteTour(tourId).subscribe(() => {
      this.router.navigate(['/admin/logistics']);
    });
  }

  private buildCostRequest(): CreateCostRequest | UpdateCostRequest {
    const v = this.costForm.getRawValue();
    // getRawValue() typuje pola jako null | T; przy form.valid pola wymagane są ustawione
    return {
      title: v.title!,
      amount: v.amount!,
      currency: v.currency!,
      date: v.date!,
      costCategoryId: v.costCategoryId?.trim() ? v.costCategoryId : null,
      tourLegId: v.tourLegId?.trim() ? v.tourLegId : null
    };
  }

  private buildRevenueRequest(): CreateRevenueRequest | UpdateRevenueRequest {
    const v = this.revenueForm.getRawValue();
    return {
      title: v.title!,
      amount: v.amount!,
      currency: v.currency!,
      date: v.date!,
      revenueCategoryId: v.revenueCategoryId?.trim() ? v.revenueCategoryId : null,
      tourLegId: v.tourLegId?.trim() ? v.tourLegId : null
    };
  }

  addCost(tourId: string) {
    if (this.costForm.valid) {
      const request = this.buildCostRequest();
      const costId = this.editingCostId();
      const operation$ = costId
        ? this.service.updateCost(tourId, costId, request as UpdateCostRequest)
        : this.service.addCost(tourId, request as CreateCostRequest);

      operation$.subscribe({
        next: () => {
          this.editingCostId.set(null);
          this.resetCostForm();
          this.loadData();
        }
      });
    }
  }

  editCost(costId: string) {
    const selectedCost = this.tour()?.costs.find(c => c.id === costId);
    if (!selectedCost) {
      return;
    }

    this.editingCostId.set(costId);
    this.costForm.patchValue({
      title: selectedCost.title,
      amount: selectedCost.amount,
      currency: selectedCost.currency,
      date: this.toDateTimeLocal(selectedCost.date),
      costCategoryId: selectedCost.costCategoryId ?? '',
      tourLegId: selectedCost.tourLegId ?? ''
    });
  }

  deleteCost(tourId: string, costId: string) {
    if (!confirm('Czy na pewno chcesz usunac koszt?')) {
      return;
    }

    this.service.deleteCost(tourId, costId).subscribe(() => {
      if (this.editingCostId() === costId) {
        this.editingCostId.set(null);
        this.resetCostForm();
      }
      this.loadData();
    });
  }

  cancelCostEdit() {
    this.editingCostId.set(null);
    this.resetCostForm();
  }

  addRevenue(tourId: string) {
    if (this.revenueForm.valid) {
      const request = this.buildRevenueRequest();
      const revenueId = this.editingRevenueId();
      const operation$ = revenueId
        ? this.service.updateRevenue(tourId, revenueId, request as UpdateRevenueRequest)
        : this.service.addRevenue(tourId, request as CreateRevenueRequest);

      operation$.subscribe({
        next: () => {
          this.editingRevenueId.set(null);
          this.resetRevenueForm();
          this.loadData();
        }
      });
    }
  }

  editRevenue(revenueId: string) {
    const selectedRevenue = this.tour()?.revenues.find(r => r.id === revenueId);
    if (!selectedRevenue) {
      return;
    }

    this.editingRevenueId.set(revenueId);
    this.revenueForm.patchValue({
      title: selectedRevenue.title,
      amount: selectedRevenue.amount,
      currency: selectedRevenue.currency,
      date: this.toDateTimeLocal(selectedRevenue.date),
      revenueCategoryId: selectedRevenue.revenueCategoryId ?? '',
      tourLegId: selectedRevenue.tourLegId ?? ''
    });
  }

  deleteRevenue(tourId: string, revenueId: string) {
    if (!confirm('Czy na pewno chcesz usunac przychod?')) {
      return;
    }

    this.service.deleteRevenue(tourId, revenueId).subscribe(() => {
      if (this.editingRevenueId() === revenueId) {
        this.editingRevenueId.set(null);
        this.resetRevenueForm();
      }
      this.loadData();
    });
  }

  cancelRevenueEdit() {
    this.editingRevenueId.set(null);
    this.resetRevenueForm();
  }

  totalCost() {
    return this.tour()?.costs.reduce((sum, c) => sum + c.amount, 0) || 0;
  }

  totalManualRevenue() {
    return this.tour()?.revenues.reduce((sum, r) => sum + r.amount, 0) || 0;
  }

  private resetCostForm() {
    this.costForm.reset({
      title: '',
      currency: 'PLN',
      amount: 0,
      date: '',
      costCategoryId: '',
      tourLegId: ''
    });
  }

  private resetRevenueForm() {
    this.revenueForm.reset({
      title: '',
      currency: 'PLN',
      amount: 0,
      date: '',
      revenueCategoryId: '',
      tourLegId: ''
    });
  }

  private toDateTimeLocal(dateValue: string | Date) {
    const date = new Date(dateValue);
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date.toISOString().slice(0, 16);
  }
}
