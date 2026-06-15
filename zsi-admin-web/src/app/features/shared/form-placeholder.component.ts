import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-form-placeholder',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="bh-form-card p-6 md:p-8 text-center">
      <p class="text-lg opacity-70 mb-4">Formularz {{ title() }} – w przygotowaniu.</p>
      <a routerLink=".." class="btn btn-ghost">← Powrót do listy</a>
    </div>
  `
})
export class FormPlaceholderComponent {
  title = input<string>('dodawania/edycji');
}
