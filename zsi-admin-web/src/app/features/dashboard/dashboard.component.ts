import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="card bg-base-100 shadow-xl">
      <div class="card-body">
        <h2 class="card-title">Witaj w Panelu!</h2>
        <p>To jest Twój dashboard startowy.</p>
        <div class="stats shadow mt-4">
          <div class="stat">
            <div class="stat-title">Sprzedaż</div>
            <div class="stat-value text-primary">25.6K</div>
            <div class="stat-desc">21% więcej niż w zeszłym miesiącu</div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent {}
