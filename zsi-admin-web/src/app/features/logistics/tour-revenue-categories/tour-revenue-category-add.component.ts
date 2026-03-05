import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TourRevenueCategoryService, CreateTourCategoryRequest } from '../../../core/services/tour-revenue-category.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tour-revenue-category-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">{{ isEdit ? 'Edytuj' : 'Dodaj' }} Kategorię Przychodów</h2>
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Kod</span></label>
          <input type="text" formControlName="code" class="input input-bordered w-full" />
        </div>
        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Nazwa</span></label>
          <input type="text" formControlName="name" class="input input-bordered w-full" />
        </div>
        <div class="form-control mb-6">
          <label class="label cursor-pointer justify-start gap-2">
            <input type="checkbox" formControlName="active" class="checkbox checkbox-sm" />
            <span class="label-text">Aktywna</span>
          </label>
        </div>
        <div class="flex justify-end gap-4">
          <a routerLink="/admin/tour-revenue-categories" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">Zapisz</button>
        </div>
      </form>
    </div>
  `
})
export class TourRevenueCategoryAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(TourRevenueCategoryService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.group({
    code: ['', Validators.required],
    name: ['', Validators.required],
    active: [true]
  });
  isSubmitting = false;
  isEdit = false;
  id: string | null = null;

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    if (this.id) {
      this.isEdit = true;
      this.service.getOne(this.id).subscribe(x => {
        this.form.patchValue({ code: x.code, name: x.name, active: x.active });
      });
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.isSubmitting = true;
    const req: CreateTourCategoryRequest = this.form.value as CreateTourCategoryRequest;
    const obs$ = this.isEdit && this.id ? this.service.update(this.id, req) : this.service.create(req);
    obs$.subscribe({
      next: () => this.router.navigate(['/admin/tour-revenue-categories']),
      error: () => { this.isSubmitting = false; }
    });
  }
}
