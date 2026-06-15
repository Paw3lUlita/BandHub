import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PaymentService, UpdatePaymentRequest } from '../../../core/services/payment.service';

const PAYMENT_STATUSES = ['PENDING', 'PAID', 'FAILED', 'REFUNDED'];

@Component({
  selector: 'app-payment-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      @if (isNewMode) {
        <h2 class="text-2xl font-bold mb-4">Nowa płatność</h2>
        <p class="text-base-content/70 mb-6">
          Płatności tworzone są automatycznie przy składaniu zamówienia. Aby oznaczyć zamówienie jako opłacone,
          użyj listy zamówień — status płatności zaktualizuje się sam.
        </p>
        <a routerLink="/admin/orders" class="btn btn-primary">Przejdź do zamówień</a>
        <a routerLink="/admin/payments" class="btn btn-ghost ml-2">Wróć do listy</a>
      } @else {
        <h2 class="text-2xl font-bold mb-6">Edytuj płatność</h2>

        @if (orderId) {
          <p class="text-sm text-base-content/70 mb-4">
            Zamówienie:
            <a [routerLink]="['/admin/orders', orderId]" class="link link-primary font-mono">{{ orderId }}</a>
          </p>
        }

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Status</span></label>
            <select formControlName="status" class="select select-bordered w-full">
              @for (s of statuses; track s) {
                <option [value]="s">{{ s }}</option>
              }
            </select>
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Dostawca płatności</span></label>
            <input type="text" formControlName="provider" class="input input-bordered w-full" placeholder="np. MANUAL"/>
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">ID transakcji u dostawcy</span></label>
            <input type="text" formControlName="providerPaymentId" class="input input-bordered w-full"/>
          </div>

          <div class="grid grid-cols-2 gap-4 mb-4">
            <div class="form-control w-full">
              <label class="label"><span class="label-text">Kwota</span></label>
              <input type="number" step="0.01" formControlName="amount" class="input input-bordered w-full"
                     [class.input-error]="isInvalid('amount')"/>
            </div>
            <div class="form-control w-full">
              <label class="label"><span class="label-text">Waluta</span></label>
              <input type="text" formControlName="currency" class="input input-bordered w-full"
                     [class.input-error]="isInvalid('currency')"/>
            </div>
          </div>

          <div class="form-control w-full mb-6">
            <label class="label"><span class="label-text">Data opłacenia</span></label>
            <input type="datetime-local" formControlName="paidAt" class="input input-bordered w-full"/>
          </div>

          <div class="flex justify-end gap-4">
            <a routerLink="/admin/payments" class="btn btn-ghost">Anuluj</a>
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
              Zapisz zmiany
            </button>
          </div>
        </form>
      }
    </div>
  `
})
export class PaymentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(PaymentService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  statuses = PAYMENT_STATUSES;
  isNewMode = false;
  isSubmitting = false;
  paymentId: string | null = null;
  orderId: string | null = null;

  form = this.fb.group({
    status: ['PENDING', Validators.required],
    provider: [''],
    providerPaymentId: [''],
    amount: [0, [Validators.required, Validators.min(0)]],
    currency: ['PLN', Validators.required],
    paidAt: ['']
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.isNewMode = !id || id === 'new';
    if (!this.isNewMode && id) {
      this.paymentId = id;
      this.service.getOne(id).subscribe(payment => {
        this.orderId = payment.orderId;
        this.form.patchValue({
          status: payment.status,
          provider: payment.provider ?? '',
          providerPaymentId: payment.providerPaymentId ?? '',
          amount: payment.amount,
          currency: payment.currency,
          paidAt: this.toDatetimeLocal(payment.paidAt)
        });
      });
    }
  }

  onSubmit() {
    if (!this.paymentId || this.form.invalid) return;

    this.isSubmitting = true;
    const v = this.form.value;
    const request: UpdatePaymentRequest = {
      id: this.paymentId,
      orderId: this.orderId!,
      status: v.status!,
      provider: v.provider || null,
      providerPaymentId: v.providerPaymentId || null,
      amount: v.amount!,
      currency: v.currency!,
      paidAt: v.paidAt ? new Date(v.paidAt).toISOString() : null
    };

    this.service.update(this.paymentId, request).subscribe({
      next: () => this.router.navigate(['/admin/payments']),
      error: (err) => {
        console.error(err);
        this.isSubmitting = false;
      }
    });
  }

  isInvalid(field: string) {
    const c = this.form.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  private toDatetimeLocal(iso: string | null): string {
    if (!iso) return '';
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}
