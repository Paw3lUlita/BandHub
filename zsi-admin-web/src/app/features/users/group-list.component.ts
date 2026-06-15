import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-group-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink],
  template: `
    <div class="bh-page overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="bh-page-title">Grupy</h2>
        <a routerLink="/admin/groups/new" class="bh-btn-primary">+ Dodaj grupę</a>
      </div>
      <table class="table table-zebra w-full">
        <thead>
          <tr class="bg-base-200">
            <th>Nazwa</th>
            <th>Ścieżka</th>
          </tr>
        </thead>
        <tbody>
          @for (group of groups$ | async; track group.id) {
            <tr class="hover">
              <td class="font-bold">{{ group.name }}</td>
              <td class="font-mono text-sm">{{ group.path }}</td>
            </tr>
          } @empty {
            <tr><td colspan="2" class="text-center py-4">Brak grup</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class GroupListComponent {
  private service = inject(UserService);
  groups$ = this.service.getGroups();
}
