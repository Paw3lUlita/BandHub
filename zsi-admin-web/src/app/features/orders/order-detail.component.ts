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
      <div class="max-w-4xl mx-auto">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold">Zamówienie #{{ order.id.slice(0, 8) }}...</h2>
            <p class="text-gray-500">Złożone: {{ order.createdAt | date:'medium' }}</p>
          </div>
          <a routerLink="/admin/orders" class="btn btn-ghost">← Wróć do listy</a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Status</h3>
              <div class="text-xl font-bold" [class.text-primary]="order.status === 'NEW'">
                {{ order.status }}
              </div>
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-gray-400">Klient</h3>
              <div class="font-bold">{{ order.userId }}</div>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl overflow-hidden">
          <div class="card-body p-0">
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
