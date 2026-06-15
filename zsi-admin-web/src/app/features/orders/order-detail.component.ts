import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderService, OrderDetails } from '../../core/services/order.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    @if (order$ | async; as order) {
      <div class="bh-page max-w-4xl mx-auto">
        <div class="bh-page-header mb-2">
          <div>
            <h2 class="bh-page-title">Zamówienie #{{ order.id.slice(0, 8) }}...</h2>
            <p class="bh-page-subtitle">Złożone: {{ order.createdAt | date:'medium' }}</p>
          </div>
          <a routerLink="/admin/orders" class="btn btn-ghost btn-sm">← Wróć do listy</a>
        </div>

        <div class="bh-detail-grid mb-6">
          <div class="bh-stat-card">
            <span class="bh-stat-label">Status</span>
            <span class="bh-stat-value" [class.text-primary]="order.status === 'NEW'">{{ order.status }}</span>
          </div>
          <div class="bh-stat-card">
            <span class="bh-stat-label">Klient</span>
            <span class="bh-stat-value text-base font-mono">{{ order.userId }}</span>
          </div>
        </div>

        @if (order.payment) {
          <div class="bh-card bh-card-body mb-4">
            <h3 class="bh-stat-label mb-2">Płatność</h3>
            <p><span class="font-medium">Status:</span> <span class="bh-status-badge badge-info">{{ order.payment.status }}</span></p>
            <p class="mt-1"><span class="font-medium">Kwota:</span> {{ order.payment.amount | currency:order.payment.currency }}</p>
            @if (order.payment.provider) { <p class="mt-1"><span class="font-medium">Provider:</span> {{ order.payment.provider }}</p> }
          </div>
        }

        @if (order.shipment) {
          <div class="bh-card bh-card-body mb-4">
            <h3 class="bh-stat-label mb-2">Wysyłka</h3>
            <p><span class="font-medium">Status:</span> <span class="bh-status-badge badge-primary">{{ order.shipment.status }}</span></p>
            @if (order.shipment.carrier) { <p class="mt-1"><span class="font-medium">Przewoźnik:</span> {{ order.shipment.carrier }}</p> }
            @if (order.shipment.trackingNumber) { <p class="mt-1"><span class="font-medium">Nr śledzenia:</span> {{ order.shipment.trackingNumber }}</p> }
            @if (order.shipment.deliveryAddress) { <p class="mt-1"><span class="font-medium">Adres:</span> {{ order.shipment.deliveryAddress }}</p> }
          </div>
        }

        @if (order.statusHistory?.length) {
          <div class="bh-card bh-card-body mb-4">
            <h3 class="bh-stat-label mb-3">Historia statusów</h3>
            <ul class="timeline timeline-vertical timeline-compact">
              @for (h of order.statusHistory; track h.id) {
                <li>
                  <hr/>
                  <div class="timeline-start text-sm font-medium">{{ h.oldStatus || '—' }} → {{ h.newStatus }}</div>
                  <div class="timeline-middle text-primary">●</div>
                  <div class="timeline-end timeline-box text-sm">
                    {{ h.changedAt | date:'short' }}
                    @if (h.changedByUsername || h.changedBy) {
                      <span class="text-xs opacity-70">({{ h.changedByUsername || h.changedBy }})</span>
                    }
                  </div>
                  <hr/>
                </li>
              }
            </ul>
          </div>
        }

        <div class="bh-table-shell">
          <table class="table table-zebra w-full">
              <thead class="bg-base-200">
                <tr>
                  <th>Produkt</th>
                  <th class="text-right">Cena jedn.</th>
                  <th class="text-center">Ilość</th>
                  <th class="text-right">Suma</th>
                </tr>
              </thead>
              <tbody>
                @for (item of order.items; track item.productId) {
                  <tr>
                    <td class="font-bold">{{ item.productName }}</td>
                    <td class="text-right">{{ item.unitPrice | currency:order.currency }}</td>
                    <td class="text-center font-bold">x{{ item.quantity }}</td>
                    <td class="text-right font-mono">{{ item.lineTotal | currency:order.currency }}</td>
                  </tr>
                }
              </tbody>
              <tfoot class="bg-base-200 font-bold text-lg">
                <tr>
                  <td colspan="3" class="text-right">RAZEM:</td>
                  <td class="text-right">{{ order.totalAmount | currency:order.currency }}</td>
                </tr>
              </tfoot>
            </table>
        </div>
      </div>
    } @else {
      <div class="flex justify-center p-10">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
    }
  `
})
export class OrderDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);

  order$!: Observable<OrderDetails>;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.order$ = this.orderService.getOrder(id);
    }
  }
}
