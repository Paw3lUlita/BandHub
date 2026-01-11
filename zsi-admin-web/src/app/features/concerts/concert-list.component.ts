import { Component, inject } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConcertService } from '../../core/services/concert.service';

@Component({
  selector: 'app-concert-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Koncerty</h2>
        <a routerLink="/admin/concerts/new" class="btn btn-primary btn-sm">
          + Dodaj Koncert
        </a>
      </div>

      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>Nazwa</th>
            <th>Data</th>
            <th>Miejsce</th>
            <th>Miasto</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (concert of concerts$ | async; track concert.id) {
            <tr class="hover">
              <td class="font-bold">{{ concert.name }}</td>
              <td>
                <div>{{ concert.date | date:'shortDate' }}</div>
                <div class="text-xs opacity-50">{{ concert.date | date:'shortTime' }}</div>
              </td>
              <td>{{ concert.venueName }}</td>
              <td>
                <span class="badge badge-ghost">{{ concert.city }}</span>
              </td>
              <td>
                <a [routerLink]="['/admin/concerts', concert.id]" class="btn btn-ghost btn-xs">
                   Szczegóły
                </a>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="5" class="text-center py-8 text-gray-500">
                Brak zaplanowanych koncertów.
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class ConcertListComponent {
  private concertService = inject(ConcertService);
  concerts$ = this.concertService.getAll();
}
