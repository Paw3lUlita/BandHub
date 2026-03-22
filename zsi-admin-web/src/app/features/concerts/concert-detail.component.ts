import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  AttendeeRow,
  ConcertDetails,
  ConcertService,
  ConcertTicketingSummary
} from '../../core/services/concert.service';
import { forkJoin, Observable, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-concert-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    @if (vm$ | async; as vm) {
      @if (vm) {
        <div class="max-w-4xl mx-auto">

          <div class="flex justify-between items-start mb-6">
            <div>
              <h2 class="text-3xl font-bold">{{ vm.concert.name }}</h2>
              <p class="text-lg text-gray-500 flex items-center gap-2">
                {{ vm.concert.date | date:'medium' }}
              </p>
              <p class="text-md text-primary font-bold">
                {{ vm.concert.venueName }}, {{ vm.concert.venueCity }}
              </p>
            </div>
            <a routerLink="/admin/concerts" class="btn btn-ghost">← Wróć do listy</a>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <div class="card bg-base-100 shadow">
              <div class="card-body py-4">
                <h3 class="text-xs uppercase text-gray-400">Sprzedane (łącznie)</h3>
                <p class="text-2xl font-bold">{{ vm.summary.totalSold }}</p>
              </div>
            </div>
            <div class="card bg-base-100 shadow">
              <div class="card-body py-4">
                <h3 class="text-xs uppercase text-gray-400">Przychód</h3>
                <p class="text-2xl font-bold">{{ vm.summary.totalRevenue | currency:vm.summary.currency:'symbol':'1.2-2' }}</p>
              </div>
            </div>
            <div class="card bg-base-100 shadow">
              <div class="card-body py-4">
                <h3 class="text-xs uppercase text-gray-400">Pojemność miejsca</h3>
                <p class="text-2xl font-bold">{{ vm.summary.venueCapacity }}</p>
              </div>
            </div>
          </div>

          <div class="flex flex-wrap gap-2 mb-6">
            <button type="button" class="btn btn-sm btn-outline" (click)="exportCsv(vm.concert.id)">
              Eksport CSV uczestników
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            @if (vm.concert.imageUrl) {
              <div class="md:col-span-1">
                <img [src]="vm.concert.imageUrl" alt="Plakat" class="rounded-box shadow-xl w-full h-auto object-cover" />
              </div>
            }

            <div [class]="vm.concert.imageUrl ? 'md:col-span-2' : 'md:col-span-3'">
              <div class="card bg-base-100 shadow-xl h-full">
                <div class="card-body">
                  <h3 class="card-title text-sm uppercase text-gray-400">Opis Wydarzenia</h3>
                  <p class="whitespace-pre-wrap">{{ vm.concert.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="card bg-base-100 shadow-xl overflow-hidden mb-8">
            <div class="card-body p-0">
              <div class="p-4 bg-base-200 font-bold border-b border-base-300">
                Sprzedaż per pula
              </div>
              <table class="table table-zebra w-full">
                <thead>
                  <tr>
                    <th>Pula</th>
                    <th class="text-right">Przychód</th>
                    <th class="text-center">Sprzedane</th>
                    <th class="text-center">Dostępne</th>
                  </tr>
                </thead>
                <tbody>
                  @for (row of vm.summary.pools; track row.poolId) {
                    <tr>
                      <td class="font-bold">{{ row.poolName }}</td>
                      <td class="text-right font-mono">{{ row.revenue | currency:row.currency:'symbol':'1.2-2' }}</td>
                      <td class="text-center">{{ row.sold }} / {{ row.total }}</td>
                      <td class="text-center">
                        <span class="badge badge-success text-white">{{ row.remaining }}</span>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>

          <div class="card bg-base-100 shadow-xl overflow-hidden">
            <div class="card-body p-0">
              <div class="p-4 bg-base-200 font-bold border-b border-base-300">
                Lista uczestników ({{ vm.attendees.length }})
              </div>
              <div class="overflow-x-auto max-h-96 overflow-y-auto">
                <table class="table table-zebra w-full table-sm">
                  <thead>
                    <tr>
                      <th>Kod</th>
                      <th>Użytkownik</th>
                      <th>Pula</th>
                      <th>Zamówienie</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (a of vm.attendees; track $index) {
                      <tr>
                        <td class="font-mono text-xs">{{ a.ticketCode }}</td>
                        <td class="text-xs">{{ a.userId }}</td>
                        <td>{{ a.poolName }}</td>
                        <td class="font-mono text-xs">{{ a.orderId }}</td>
                      </tr>
                    } @empty {
                      <tr><td colspan="4" class="text-center py-4">Brak wydanych biletów</td></tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          </div>

        </div>
      } @else {
        <p class="p-6">Nieprawidłowy identyfikator koncertu.</p>
      }
    } @else {
      <div class="flex justify-center p-10">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
    }
  `
})
export class ConcertDetailComponent {
  private route = inject(ActivatedRoute);
  private concertService = inject(ConcertService);

  vm$: Observable<{
    concert: ConcertDetails;
    summary: ConcertTicketingSummary;
    attendees: AttendeeRow[];
  } | null> = this.route.paramMap.pipe(
    switchMap((pm) => {
      const id = pm.get('id');
      if (!id) {
        return of(null);
      }
      return forkJoin({
        concert: this.concertService.getOne(id),
        summary: this.concertService.getTicketingSummary(id),
        attendees: this.concertService.getTicketingAttendees(id)
      });
    })
  );

  exportCsv(concertId: string) {
    this.concertService.downloadAttendeesCsv(concertId).subscribe((blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `attendees-${concertId}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    });
  }
}
