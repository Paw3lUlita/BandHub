import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // AsyncPipe, DatePipe, etc.
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ConcertDetails, ConcertService } from '../../core/services/concert.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-concert-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    @if (concert$ | async; as concert) {
      <div class="max-w-4xl mx-auto">

        <div class="flex justify-between items-start mb-6">
          <div>
            <h2 class="text-3xl font-bold">{{ concert.name }}</h2>
            <p class="text-lg text-gray-500 flex items-center gap-2">
              📅 {{ concert.date | date:'medium' }}
            </p>
            <p class="text-md text-primary font-bold">
              📍 {{ concert.venueName }}, {{ concert.venueCity }}
            </p>
          </div>
          <a routerLink="/admin/concerts" class="btn btn-ghost">← Wróć do listy</a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          @if (concert.imageUrl) {
            <div class="md:col-span-1">
              <img [src]="concert.imageUrl" alt="Plakat" class="rounded-box shadow-xl w-full h-auto object-cover" />
            </div>
          }

          <div [class]="concert.imageUrl ? 'md:col-span-2' : 'md:col-span-3'">
            <div class="card bg-base-100 shadow-xl h-full">
              <div class="card-body">
                <h3 class="card-title text-sm uppercase text-gray-400">Opis Wydarzenia</h3>
                <p class="whitespace-pre-wrap">{{ concert.description }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="card bg-base-100 shadow-xl overflow-hidden">
          <div class="card-body p-0">
            <div class="p-4 bg-base-200 font-bold border-b border-base-300">
              🎫 Pule Biletów
            </div>
            <table class="table table-zebra w-full">
              <thead>
                <tr>
                  <th>Nazwa Puli</th>
                  <th class="text-right">Cena</th>
                  <th class="text-center">Dostępne</th>
                  <th class="text-center">Sprzedane</th>
                </tr>
              </thead>
              <tbody>
                @for (pool of concert.ticketPools; track pool.id) {
                  <tr>
                    <td class="font-bold">{{ pool.name }}</td>
                    <td class="text-right font-mono">
                      {{ pool.price | currency:pool.currency }}
                    </td>
                    <td class="text-center">
                      <span class="badge badge-success text-white">
                        {{ pool.remainingQuantity }} / {{ pool.totalQuantity }}
                      </span>
                    </td>
                    <td class="text-center text-gray-500">
                      {{ pool.totalQuantity - pool.remainingQuantity }}
                    </td>
                  </tr>
                }
              </tbody>
            </table>
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
export class ConcertDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private concertService = inject(ConcertService);

  concert$!: Observable<ConcertDetails>;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.concert$ = this.concertService.getOne(id);
    }
  }
}
