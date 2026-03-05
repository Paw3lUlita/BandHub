import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  UserService,
  UserResponse,
  RoleResponse,
  GroupResponse,
  UpdateUserRequest,
  ResetPasswordRequest
} from '../../core/services/user.service';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    @if (user(); as u) {
      <div class="max-w-4xl mx-auto">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold">{{ u.username }}</h2>
            <p class="text-base-content/70 text-sm">{{ u.firstName }} {{ u.lastName }} · {{ u.email || 'brak email' }}</p>
          </div>
          <a routerLink="/admin/users" class="btn btn-ghost">← Wróć do listy</a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Status</h3>
              @if (u.enabled) {
                <span class="badge badge-success">Aktywny</span>
                <button class="btn btn-ghost btn-sm mt-2" (click)="setEnabled(false)">Dezaktywuj</button>
              } @else {
                <span class="badge badge-ghost">Nieaktywny</span>
                <button class="btn btn-ghost btn-sm mt-2" (click)="setEnabled(true)">Aktywuj</button>
              }
            </div>
          </div>
          <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
              <h3 class="card-title text-sm uppercase text-base-content/60">Utworzono</h3>
              <div>{{ u.createdTimestamp | date:'medium' }}</div>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl mb-6">
          <div class="card-body">
            <h3 class="card-title">Edycja danych</h3>
            <form [formGroup]="editForm" (ngSubmit)="onUpdateUser()">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="form-control">
                  <label class="label"><span class="label-text">Imię</span></label>
                  <input type="text" formControlName="firstName" class="input input-bordered"/>
                </div>
                <div class="form-control">
                  <label class="label"><span class="label-text">Nazwisko</span></label>
                  <input type="text" formControlName="lastName" class="input input-bordered"/>
                </div>
                <div class="form-control md:col-span-2">
                  <label class="label"><span class="label-text">Email</span></label>
                  <input type="email" formControlName="email" class="input input-bordered"/>
                </div>
              </div>
              <button type="submit" class="btn btn-primary btn-sm mt-4" [disabled]="editForm.invalid || isUpdating">Zapisz</button>
            </form>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl mb-6">
          <div class="card-body">
            <h3 class="card-title">Reset hasła</h3>
            <form [formGroup]="passwordForm" (ngSubmit)="onResetPassword()">
              <div class="form-control max-w-xs">
                <label class="label"><span class="label-text">Nowe hasło</span></label>
                <input type="password" formControlName="password" class="input input-bordered" placeholder="min. 8 znaków"/>
              </div>
              <label class="label cursor-pointer justify-start gap-2 mt-2">
                <input type="checkbox" formControlName="temporary" class="checkbox checkbox-sm"/>
                <span class="label-text">Wymuszenie zmiany przy pierwszym logowaniu</span>
              </label>
              <button type="submit" class="btn btn-warning btn-sm mt-4" [disabled]="passwordForm.invalid || isResetting">Resetuj hasło</button>
            </form>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl mb-6">
          <div class="card-body">
            <h3 class="card-title">Role</h3>
            <div class="flex flex-wrap gap-2 mb-4">
              @for (r of userRoles(); track r.name) {
                <span class="badge badge-primary gap-1">
                  {{ r.name }}
                  <button type="button" class="btn btn-ghost btn-xs p-0 min-h-0 h-auto" (click)="removeRole(r.name)" aria-label="Usuń rolę">×</button>
                </span>
              } @empty {
                <span class="text-base-content/60 text-sm">Brak przypisanych ról</span>
              }
            </div>
            <div class="flex gap-2">
              <select class="select select-bordered select-sm" [value]="selectedRole()" (change)="selectedRole.set($any($event.target).value)">
                <option value="">— Wybierz rolę —</option>
                @for (r of rolesToAssign(); track r.name) {
                  <option [value]="r.name">{{ r.name }}</option>
                }
              </select>
              <button class="btn btn-primary btn-sm" (click)="assignRole()" [disabled]="!selectedRole()">Dodaj rolę</button>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl">
          <div class="card-body">
            <h3 class="card-title">Grupy</h3>
            <div class="flex flex-wrap gap-2 mb-4">
              @for (g of userGroups(); track g.id) {
                <span class="badge badge-secondary gap-1">
                  {{ g.name }}
                  <button type="button" class="btn btn-ghost btn-xs p-0 min-h-0 h-auto" (click)="removeGroup(g.id)" aria-label="Usuń grupę">×</button>
                </span>
              } @empty {
                <span class="text-base-content/60 text-sm">Brak przypisanych grup</span>
              }
            </div>
            <div class="flex gap-2">
              <select class="select select-bordered select-sm" [value]="selectedGroup()" (change)="selectedGroup.set($any($event.target).value)">
                <option value="">— Wybierz grupę —</option>
                @for (g of groupsToAssign(); track g.id) {
                  <option [value]="g.id">{{ g.name }}</option>
                }
              </select>
              <button class="btn btn-secondary btn-sm" (click)="assignGroup()" [disabled]="!selectedGroup()">Dodaj grupę</button>
            </div>
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
export class UserDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private service = inject(UserService);

  user = signal<UserResponse | null>(null);
  userRoles = signal<RoleResponse[]>([]);
  userGroups = signal<GroupResponse[]>([]);
  availableRoles = signal<RoleResponse[]>([]);
  availableGroups = signal<GroupResponse[]>([]);

  rolesToAssign = signal<RoleResponse[]>([]);
  groupsToAssign = signal<GroupResponse[]>([]);
  selectedRole = signal('');
  selectedGroup = signal('');
  isUpdating = false;
  isResetting = false;

  editForm = this.fb.group({
    firstName: [''],
    lastName: [''],
    email: ['']
  });

  passwordForm = this.fb.group({
    password: ['', [Validators.required, Validators.minLength(8)]],
    temporary: [true]
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadUser(id);
      this.service.getRealmRoles().subscribe(roles => {
        this.availableRoles.set(roles);
        this.updateRolesToAssign();
      });
      this.service.getGroups().subscribe(groups => {
        this.availableGroups.set(groups);
        this.updateGroupsToAssign();
      });
    }
  }

  private updateRolesToAssign() {
    const assigned = new Set(this.userRoles().map(r => r.name));
    this.rolesToAssign.set(this.availableRoles().filter(r => !assigned.has(r.name)));
  }

  private updateGroupsToAssign() {
    const assigned = new Set(this.userGroups().map(g => g.id));
    this.groupsToAssign.set(this.availableGroups().filter(g => !assigned.has(g.id)));
  }

  private loadUser(id: string) {
    this.service.getUser(id).subscribe(u => {
      this.user.set(u);
      this.editForm.patchValue({
        firstName: u.firstName || '',
        lastName: u.lastName || '',
        email: u.email || ''
      });
    });
    this.service.getUserRoles(id).subscribe(roles => {
      this.userRoles.set(roles);
      this.updateRolesToAssign();
    });
    this.service.getUserGroups(id).subscribe(groups => {
      this.userGroups.set(groups);
      this.updateGroupsToAssign();
    });
  }

  onUpdateUser() {
    const id = this.user()?.id;
    if (!id || !this.editForm.valid) return;
    this.isUpdating = true;
    const v = this.editForm.value;
    const req: UpdateUserRequest = {
      firstName: v.firstName || undefined,
      lastName: v.lastName || undefined,
      email: v.email || undefined
    };
    this.service.updateUser(id, req).subscribe({
      next: () => {
        this.service.getUser(id).subscribe(u => this.user.set(u));
        this.isUpdating = false;
      },
      error: () => this.isUpdating = false
    });
  }

  onResetPassword() {
    const id = this.user()?.id;
    if (!id || !this.passwordForm.valid) return;
    this.isResetting = true;
    const v = this.passwordForm.value;
    const req: ResetPasswordRequest = { password: v.password!, temporary: v.temporary ?? true };
    this.service.resetPassword(id, req).subscribe({
      next: () => {
        this.passwordForm.reset({ password: '', temporary: true });
        this.isResetting = false;
      },
      error: () => this.isResetting = false
    });
  }

  setEnabled(enabled: boolean) {
    const id = this.user()?.id;
    if (!id) return;
    this.service.setEnabled(id, enabled).subscribe({
      next: () => this.service.getUser(id).subscribe(u => this.user.set(u))
    });
  }

  assignRole() {
    const id = this.user()?.id;
    const roleName = this.selectedRole();
    if (!id || !roleName) return;
    this.service.assignRole(id, roleName).subscribe({
      next: () => {
        this.service.getUserRoles(id).subscribe(r => {
          this.userRoles.set(r);
          this.updateRolesToAssign();
        });
        this.selectedRole.set('');
      }
    });
  }

  removeRole(roleName: string) {
    const id = this.user()?.id;
    if (!id) return;
    this.service.removeRole(id, roleName).subscribe({
      next: () => this.service.getUserRoles(id).subscribe(r => {
        this.userRoles.set(r);
        this.updateRolesToAssign();
      })
    });
  }

  assignGroup() {
    const id = this.user()?.id;
    const groupId = this.selectedGroup();
    if (!id || !groupId) return;
    this.service.assignGroup(id, groupId).subscribe({
      next: () => {
        this.service.getUserGroups(id).subscribe(g => {
          this.userGroups.set(g);
          this.updateGroupsToAssign();
        });
        this.selectedGroup.set('');
      }
    });
  }

  removeGroup(groupId: string) {
    const id = this.user()?.id;
    if (!id) return;
    this.service.removeGroup(id, groupId).subscribe({
      next: () => this.service.getUserGroups(id).subscribe(g => {
        this.userGroups.set(g);
        this.updateGroupsToAssign();
      })
    });
  }
}
