import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CreateShipmentRequest, ShipmentService } from '../../../core/services/shipment.service';

const SHIPMENT_STATUSES = ['PENDING', 'SHIPPED', 'DELIVERED'];

@Component({
  selector: 'app-shipment-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      @if (isNewMode) {
        <h2 class="text-2xl font-bold mb-4">Nowa wysyłka</h2>
        <p class="text-base-content/70 mb-6">
          Wysyłki tworzone są automatycznie przy składaniu zamówienia. Aby oznaczyć zamówienie jako wysłane
          lub dostarczone, użyj listy zamówień — status wysyłki zaktualizuje się sam.
        </p>
        <a routerLink="/admin/orders" class="btn btn-primary">Przejdź do zamówień</a>
        <a routerLink="/admin/shipments" class="btn btn-ghost ml-2">Wróć do listy</a>
      } @else {
        <h2 class="text-2xl font-bold mb-6">Edytuj wysyłkę</h2>

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
            <label class="label"><span class="label-text">Kurier</span></label>
            <input type="text" formControlName="carrier" class="input input-bordered w-full" placeholder="np. InPost"/>
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Numer śledzenia</span></label>
            <input type="text" formControlName="trackingNumber" class="input input-bordered w-full"/>
          </div>

          <div class="form-control w-full mb-4">
            <label class="label"><span class="label-text">Adres dostawy</span></label>
            <textarea formControlName="deliveryAddress" class="textarea textarea-bordered w-full" rows="3"></textarea>
          </div>

          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="form-control w-full">
              <label class="label"><span class="label-text">Data wysłania</span></label>
              <input type="datetime-local" formControlName="shippedAt" class="input input-bordered w-full"/>
            </div>
            <div class="form-control w-full">
              <label class="label"><span class="label-text">Data dostarczenia</span></label>
              <input type="datetime-local" formControlName="deliveredAt" class="input input-bordered w-full"/>
            </div>
          </div>

          <div class="flex justify-end gap-4">
            <a routerLink="/admin/shipments" class="btn btn-ghost">Anuluj</a>
            <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
              Zapisz zmiany
            </button>
          </div>
        </form>
      }
    </div>
  `
})
export class ShipmentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(ShipmentService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  statuses = SHIPMENT_STATUSES;
  isNewMode = false;
  isSubmitting = false;
  shipmentId: string | null = null;
  orderId: string | null = null;

  form = this.fb.group({
    status: ['PENDING', Validators.required],
    carrier: [''],
    trackingNumber: [''],
    deliveryAddress: [''],
    shippedAt: [''],
    deliveredAt: ['']
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.isNewMode = !id;
    if (!this.isNewMode && id) {
      this.shipmentId = id;
      this.service.getOne(id).subscribe(shipment => {
        this.orderId = shipment.orderId;
        this.form.patchValue({
          status: shipment.status,
          carrier: shipment.carrier ?? '',
          trackingNumber: shipment.trackingNumber ?? '',
          deliveryAddress: shipment.deliveryAddress ?? '',
          shippedAt: this.toDatetimeLocal(shipment.shippedAt),
          deliveredAt: this.toDatetimeLocal(shipment.deliveredAt)
        });
      });
    }
  }

  onSubmit() {
    if (!this.shipmentId || !this.orderId || this.form.invalid) return;

    this.isSubmitting = true;
    const v = this.form.value;
    const request: CreateShipmentRequest = {
      id: this.shipmentId,
      orderId: this.orderId,
      status: v.status!,
      carrier: v.carrier || null,
      trackingNumber: v.trackingNumber || null,
      deliveryAddress: v.deliveryAddress || null,
      shippedAt: v.shippedAt ? new Date(v.shippedAt).toISOString() : null,
      deliveredAt: v.deliveredAt ? new Date(v.deliveredAt).toISOString() : null
    };

    this.service.update(this.shipmentId, request).subscribe({
      next: () => this.router.navigate(['/admin/shipments']),
      error: (err) => {
        console.error(err);
        this.isSubmitting = false;
      }
    });
  }

  private toDatetimeLocal(iso: string | null): string {
    if (!iso) return '';
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}
