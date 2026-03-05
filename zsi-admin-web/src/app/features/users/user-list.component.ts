import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Użytkownicy</h2>
        <a routerLink="/admin/users/new" class="btn btn-primary btn-sm">+ Dodaj użytkownika</a>
      </div>
      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>Login</th>
            <th>Imię</th>
            <th>Nazwisko</th>
            <th>Email</th>
            <th>Status</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (user of users$ | async; track user.id) {
            <tr class="hover">
              <td class="font-mono">{{ user.username }}</td>
              <td>{{ user.firstName || '-' }}</td>
              <td>{{ user.lastName || '-' }}</td>
              <td>{{ user.email || '-' }}</td>
              <td>
                @if (user.enabled) {
                  <span class="badge badge-success badge-sm">Aktywny</span>
                } @else {
                  <span class="badge badge-ghost badge-sm">Nieaktywny</span>
                }
              </td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/users', user.id]" class="btn btn-ghost btn-xs">Szczegóły</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(user.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="6" class="text-center py-4">Brak użytkowników</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class UserListComponent {
  private service = inject(UserService);
  users$ = this.service.getAllUsers();

  onDelete(id: string) {
    if (confirm('Usunąć użytkownika?')) {
      this.service.deleteUser(id).subscribe({
        next: () => {
          this.users$ = this.service.getAllUsers();
        },
        error: (err) => console.error(err)
      });
    }
  }
}
