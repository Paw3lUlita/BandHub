import {Component, inject} from '@angular/core';
import {AsyncPipe, CurrencyPipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {ProductService} from '../../core/services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Produkty</h2>
        <a routerLink="/admin/products/new" class="btn btn-primary btn-sm">
          + Dodaj Produkt
        </a>
      </div>

      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
        <tr class="bg-base-200">
          <th>Nazwa</th>
          <th>Kategoria</th>
          <th>Cena</th>
          <th>Stan</th>
          <th>Akcje</th>
        </tr>
        </thead>
        <tbody>
        @for (product of products$ | async; track product.id) {
        <tr class="hover">
          <td>
            <div class="font-bold">{{ product.name }}</div>
                <div class="text-xs opacity-50">{{ product.id }}</div>
              </td>
              <td>
                <span class="badge badge-ghost badge-sm">{{ product.categoryName }}</span>
              </td>
              <td class="font-mono">{{ product.price | currency:'PLN':'symbol':'1.2-2' }}</td>
              <td>
                @if (product.stockQuantity > 0) {
        <span class="text-success font-bold">{{ product.stockQuantity }} szt.</span>
        } @else {
        <span class="text-error">Brak</span>
        }
        </td>
        <td>
          <a [routerLink]="['/admin/products', product.id]" class="btn btn-ghost btn-xs">Edytuj</a>
          <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(product.id)">Usuń</button>
        </td>
      </tr>
        } @empty {
        <tr>
          <td colspan="5" class="text-center py-8 text-gray-500">
            Brak produktów lub problem z połączeniem z API.
          </td>
        </tr>
        }
        </tbody>
      </table>
    </div>
  `
})
export class ProductListComponent {
  private productService = inject(ProductService);

  // Bezpośrednio przypisujemy Observable do zmiennej (AsyncPipe to obsłuży)
  products$ = this.productService.getProducts();

  onDelete(id: string) {
    if (confirm('Czy na pewno usunąć ten produkt?')) {
      this.productService.deleteProduct(id).subscribe(() => {
        // Odśwież listę po usunięciu (prosty trick: ponowne przypisanie observable)
        this.products$ = this.productService.getProducts();
      });
    }
  }
}
