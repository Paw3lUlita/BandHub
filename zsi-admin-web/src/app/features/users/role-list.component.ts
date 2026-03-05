import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Role</h2>
        <a routerLink="/admin/roles/new" class="btn btn-primary btn-sm">+ Dodaj rolę</a>
      </div>
      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
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
