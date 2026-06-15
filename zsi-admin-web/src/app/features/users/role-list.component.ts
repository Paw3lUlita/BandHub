import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink],
  template: `
    <div class="bh-page overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="bh-page-title">Role</h2>
        <a routerLink="/admin/roles/new" class="bh-btn-primary">+ Dodaj rolę</a>
      </div>
      <table class="table table-zebra w-full">
        <thead>
          <tr class="bg-base-200">
            <th>Nazwa</th>
            <th>Opis</th>
          </tr>
        </thead>
        <tbody>
          @for (role of roles$ | async; track role.id) {
            <tr class="hover">
              <td class="font-mono font-bold">{{ role.name }}</td>
              <td>{{ role.description || '-' }}</td>
            </tr>
          } @empty {
            <tr><td colspan="2" class="text-center py-4">Brak ról</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class RoleListComponent {
  private service = inject(UserService);
  roles$ = this.service.getRealmRoles();
}
