import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CategoryService, CreateCategoryRequest } from '../../core/services/category.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-category-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">{{ isEditMode ? 'Edytuj' : 'Dodaj' }} Kategorię</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-control w-full mb-6">
          <label class="label"><span class="label-text">Nazwa kategorii</span></label>
          <input type="text" formControlName="name" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('name')" placeholder="np. Elektronika"/>
          @if (isInvalid('name')) { <span class="text-error text-xs mt-1">Nazwa wymagana</span> }
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/categories" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            {{ isEditMode ? 'Zapisz Zmiany' : 'Utwórz' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class CategoryAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(CategoryService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]]
  });

  isSubmitting = false;
  isEditMode = false;
  categoryId: string | null = null;

  ngOnInit() {
    this.categoryId = this.route.snapshot.paramMap.get('id');
    if (this.categoryId) {
      this.isEditMode = true;
      this.service.getOne(this.categoryId).subscribe(cat => {
        this.form.patchValue({ name: cat.name });
      });
    }
  }

  onSubmit() {
    if (this.form.valid) {
      this.isSubmitting = true;
      const request: CreateCategoryRequest = { name: this.form.value.name! };

      const obs$ = this.isEditMode && this.categoryId
        ? this.service.update(this.categoryId, request)
        : this.service.create(request);

      obs$.subscribe({
        next: () => this.router.navigate(['/admin/categories']),
        error: (err) => {
          console.error(err);
          this.isSubmitting = false;
        }
      });
    }
  }

  isInvalid(field: string) {
    const c = this.form.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }
}
