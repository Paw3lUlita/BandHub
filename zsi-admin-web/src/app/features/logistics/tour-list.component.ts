import { Component, inject } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LogisticsService } from '../../core/services/logistics.service';

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Trasy Koncertowe</h2>
        <a routerLink="/admin/logistics/new" class="btn btn-primary btn-sm">
          + Zaplanuj Trasę
        </a>
      </div>

      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>Nazwa Trasy</th>
            <th>Start</th>
            <th>Koniec</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (tour of tours$ | async; track tour.id) {
            <tr class="hover">
              <td class="font-bold text-lg">{{ tour.name }}</td>
              <td>{{ tour.startDate | date:'shortDate' }}</td>
              <td>{{ tour.endDate | date:'shortDate' }}</td>
              <td>
                <a [routerLink]="['/admin/logistics', tour.id]" class="btn btn-ghost btn-sm text-primary">
                  Panel Logistyki
                </a>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="4" class="text-center py-8 text-gray-500">
                Brak zaplanowanych tras.
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class TourListComponent {
  private service = inject(LogisticsService);
  tours$ = this.service.getAllTours();
}
