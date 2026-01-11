import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VenueService } from '../../core/services/venue.service';

@Component({
  selector: 'app-venue-list',
  standalone: true,
  imports: [AsyncPipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Miejsca Koncertowe</h2>
        <a routerLink="/admin/venues/new" class="btn btn-primary btn-sm">
          + Dodaj Miejsce
        </a>
      </div>

      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
        <tr class="bg-base-200">
          <th>Nazwa</th>
          <th>Miasto</th>
          <th>Adres</th>
          <th>Pojemność</th>
          <th>Email</th>
          <th>Akcje</th>
        </tr>
        </thead>
        <tbody>
        @for (venue of venues$ | async; track venue.id) {
        <tr class="hover">
          <td class="font-bold">{{ venue.name }}</td>
          <td>{{ venue.city }}</td>
          <td>{{ venue.street }}</td>
          <td>
            <div class="badge badge-outline">{{ venue.capacity }}</div>
          </td>
          <td>{{ venue.contactEmail }}</td>
          <td>
            <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(venue.id)">Usuń</button>
          </td>
        </tr>
        } @empty {
        <tr>
          <td colspan="6" class="text-center py-8 text-gray-500">
            Brak zdefiniowanych miejsc.
          </td>
        </tr>
        }
        </tbody>
      </table>
    </div>
  `
})
export class VenueListComponent {
  private venueService = inject(VenueService);

  venues$ = this.venueService.getAll();

  onDelete(id: string) {
    if (confirm('Czy na pewno usunąć to miejsce?')) {
      this.venueService.delete(id).subscribe({
        next: () => {
          // Odśwież listę
          this.venues$ = this.venueService.getAll();
        },
        error: (err) => console.error('Błąd usuwania', err)
      });
    }
  }
}
