import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService, CreateUserRequest } from '../../core/services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  template: `
    <div class="max-w-lg mx-auto bg-base-100 shadow-xl rounded-box p-6">
      <h2 class="text-2xl font-bold mb-6">Dodaj użytkownika</h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Login *</span></label>
          <input type="text" formControlName="username" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('username')" placeholder="np. jan.kowalski"/>
          @if (isInvalid('username')) { <span class="text-error text-xs mt-1">Login wymagany</span> }
        </div>

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Hasło *</span></label>
          <input type="password" formControlName="password" class="input input-bordered w-full"
                 [class.input-error]="isInvalid('password')" placeholder="min. 8 znaków"/>
          @if (isInvalid('password')) { <span class="text-error text-xs mt-1">Hasło min. 8 znaków</span> }
        </div>

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Imię</span></label>
          <input type="text" formControlName="firstName" class="input input-bordered w-full"/>
        </div>

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Nazwisko</span></label>
          <input type="text" formControlName="lastName" class="input input-bordered w-full"/>
        </div>

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Email</span></label>
          <input type="email" formControlName="email" class="input input-bordered w-full"/>
        </div>

        <div class="form-control mb-6">
          <label class="label cursor-pointer justify-start gap-2">
            <input type="checkbox" formControlName="enabled" class="checkbox checkbox-sm"/>
            <span class="label-text">Konto aktywne</span>
          </label>
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/users" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            Utwórz
          </button>
        </div>
      </form>
    </div>
  `
})
export class UserAddComponent {
  private fb = inject(FormBuilder);
  private service = inject(UserService);
  private router = inject(Router);

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(1)]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    firstName: [''],
    lastName: [''],
    email: [''],
    enabled: [true]
  });

  isSubmitting = false;

  onSubmit() {
    if (this.form.valid) {
      this.isSubmitting = true;
      const v = this.form.value;
      const request: CreateUserRequest = {
        username: v.username!,
        password: v.password!,
        firstName: v.firstName || undefined,
        lastName: v.lastName || undefined,
        email: v.email || undefined,
        enabled: v.enabled ?? true
      };

      this.service.createUser(request).subscribe({
        next: () => this.router.navigate(['/admin/users']),
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
