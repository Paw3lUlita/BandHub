import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService, CreateProductRequest } from '../../core/services/product.service';

@Component({
  selector: 'app-product-add',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-2xl mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">
        {{ isEditMode ? 'Edytuj Produkt' : 'Dodaj Nowy Produkt' }}
      </h2>

      <form [formGroup]="productForm" (ngSubmit)="onSubmit()">

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Nazwa produktu</span></label>
          <input type="text" formControlName="name" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('name')" />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div class="form-control">
            <label class="label"><span class="label-text">Cena</span></label>
            <input type="number" formControlName="price" class="input input-bordered"
                   [class.input-error]="isInvalid('price')" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Waluta</span></label>
            <input type="text" formControlName="currency" class="input input-bordered" readonly />
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div class="form-control">
            <label class="label"><span class="label-text">Ilość (Sztuki)</span></label>
            <input type="number" formControlName="stockQuantity" class="input input-bordered"
                   [class.input-error]="isInvalid('stockQuantity')" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">ID Kategorii (UUID)</span></label>
            <input type="text" formControlName="categoryId" class="input input-bordered"
                   [class.input-error]="isInvalid('categoryId')" />
          </div>
        </div>

        <div class="form-control w-full mb-6">
          <label class="label"><span class="label-text">Opis</span></label>
          <textarea formControlName="description" class="textarea textarea-bordered h-24"></textarea>
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/products" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="productForm.invalid || isSubmitting">
            @if(isSubmitting) { <span class="loading loading-spinner"></span> }
            {{ isEditMode ? 'Zaktualizuj' : 'Zapisz Produkt' }}
          </button>
        </div>

      </form>
    </div>
  `
})
export class ProductAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private productService = inject(ProductService);
  private router = inject(Router);
  private route = inject(ActivatedRoute); // Do czytania URL

  isSubmitting = false;
  isEditMode = false;
  productId: string | null = null;

  productForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    price: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['PLN', Validators.required],
    stockQuantity: [0, [Validators.required, Validators.min(0)]],
    categoryId: ['', [Validators.required, Validators.minLength(36)]]
  });

  ngOnInit() {
    // Sprawdzamy czy w adresie jest ID (np. /products/e282...)
    this.productId = this.route.snapshot.paramMap.get('id');

    if (this.productId) {
      this.isEditMode = true;
      this.loadProductData(this.productId);
    }
  }

  loadProductData(id: string) {
    this.productService.getProduct(id).subscribe({
      next: (product) => {
        // Wypełniamy formularz danymi z backendu
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          currency: product.currency,
          stockQuantity: product.stockQuantity ?? 0, // Fallback jeśli null
          // UWAGA: Tu jest problem - Backend zwraca categoryName, a my potrzebujemy ID do edycji.
          // Jeśli backend nie zwraca categoryId w GET, będziesz musiał wpisać je ręcznie ponownie.
          // Na razie zostawiam puste lub to co przyjdzie.
          categoryId: ''
        });
      },
      error: (err) => console.error('Błąd pobierania produktu', err)
    });
  }

  isInvalid(field: string): boolean {
    const control = this.productForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.productForm.invalid) return;

    this.isSubmitting = true;
    const request = this.productForm.value as unknown as CreateProductRequest;

    if (this.isEditMode && this.productId) {
      // EDYCJA (PUT)
      this.productService.updateProduct(this.productId, request).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => this.handleError(err)
      });
    } else {
      // DODAWANIE (POST)
      this.productService.createProduct(request).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => this.handleError(err)
      });
    }
  }

  private handleSuccess() {
    console.log('✅ Sukces');
    this.router.navigate(['/admin/products']);
  }

  private handleError(err: any) {
    console.error('❌ Błąd:', err);
    this.isSubmitting = false;
    alert('Wystąpił błąd zapisu.');
  }
}
