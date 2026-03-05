import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-group-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">Dodaj grupę</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Nazwa *</span></label>
          <input type="text" formControlName="name" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('name')" placeholder="np. VIP"/>
          @if (isInvalid('name')) { <span class="text-error text-xs mt-1">Nazwa wymagana</span> }
        </div>

        <div class="form-control w-full mb-6">
          <label class="label"><span class="label-text">Ścieżka</span></label>
          <input type="text" formControlName="path" class="input input-bordered w-full"
                 placeholder="np. /vip (opcjonalnie)"/>
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/groups" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            Utwórz
          </button>
        </div>
      </form>
    </div>
  `
})
export class GroupAddComponent {
  private fb = inject(FormBuilder);
  private service = inject(UserService);
  private router = inject(Router);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(1)]],
    path: ['']
  });

  isSubmitting = false;

  onSubmit() {
    if (this.form.valid) {
      this.isSubmitting = true;
      const v = this.form.value;
      this.service.createGroup({
        name: v.name!,
        path: v.path || undefined
      }).subscribe({
        next: () => this.router.navigate(['/admin/groups']),
        error: () => this.isSubmitting = false
      });
    }
  }

  isInvalid(field: string) {
    const c = this.form.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }
}
