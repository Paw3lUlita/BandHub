import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="bh-page-header">
      <div>
        <h2 class="bh-page-title">{{ title() }}</h2>
        @if (subtitle()) {
          <p class="bh-page-subtitle">{{ subtitle() }}</p>
        }
      </div>
      @if (actionLabel() && actionLink()) {
        <a [routerLink]="actionLink()!" class="bh-btn-primary">
          {{ actionLabel() }}
        </a>
      }
      <ng-content />
    </div>
  `,
})
export class PageHeaderComponent {
  title = input.required<string>();
  subtitle = input<string>();
  actionLabel = input<string>();
  actionLink = input<string>();
}
