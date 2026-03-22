import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TicketScanService, ScanTicketResponse } from '../../../core/services/ticket-scan.service';

@Component({
  selector: 'app-ticket-scan',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="max-w-lg">
      <h2 class="text-2xl font-bold mb-6">Walidacja biletu (skan)</h2>

      <div class="card bg-base-100 shadow-xl">
        <div class="card-body gap-4">
          <div class="form-control">
            <label class="label"><span class="label-text">Kod biletu</span></label>
            <input type="text" class="input input-bordered" [(ngModel)]="codeValue" placeholder="Wklej lub zeskanuj kod" />
          </div>
          <div class="form-control">
            <label class="label"><span class="label-text">Brama (opcjonalnie)</span></label>
            <input type="text" class="input input-bordered" [(ngModel)]="gateName" placeholder="np. A1" />
          </div>
          <button class="btn btn-primary" [disabled]="loading() || !codeValue.trim()" (click)="submit()">
            @if (loading()) {
              <span class="loading loading-spinner loading-sm"></span>
            } @else {
              Zarejestruj wejście
            }
          </button>

          @if (result()) {
            <div class="alert" [class.alert-success]="result()!.valid" [class.alert-error]="!result()!.valid">
              <div>
                <p class="font-bold">{{ result()!.message }}</p>
                <p class="text-sm">Wynik: {{ result()!.result }}</p>
                @if (result()!.concertName) {
                  <p class="text-sm">Koncert: {{ result()!.concertName }}</p>
                }
                @if (result()!.poolName) {
                  <p class="text-sm">Pula: {{ result()!.poolName }}</p>
                }
              </div>
            </div>
          }
        </div>
      </div>
    </div>
  `
})
export class TicketScanComponent {
  private scanService = inject(TicketScanService);

  codeValue = '';
  gateName = '';
  loading = signal(false);
  result = signal<ScanTicketResponse | null>(null);

  submit() {
    this.loading.set(true);
    this.result.set(null);
    this.scanService
      .scan({
        codeValue: this.codeValue.trim(),
        gateName: this.gateName.trim() || undefined
      })
      .subscribe({
        next: (r) => {
          this.result.set(r);
          this.loading.set(false);
        },
        error: () => this.loading.set(false)
      });
  }
}
