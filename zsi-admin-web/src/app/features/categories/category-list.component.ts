import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common'; // AsyncPipe
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../core/services/category.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="overflow-x-auto">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold">Kategorie</h2>
        <a routerLink="/admin/categories/new" class="btn btn-primary btn-sm">+ Dodaj Kategorię</a>
      </div>

      <table class="table table-zebra bg-base-100 shadow-lg rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>ID</th>
            <th>Nazwa</th>
            <th class="w-32">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (cat of categories$ | async; track cat.id) {
            <tr class="hover">
              <td class="text-xs opacity-50 font-mono">{{ cat.id }}</td>
              <td class="font-bold">{{ cat.name }}</td>
              <td class="flex gap-2">
                <a [routerLink]="['/admin/categories', cat.id]" class="btn btn-ghost btn-xs">Edytuj</a>
                <button class="btn btn-ghost btn-xs text-error" (click)="onDelete(cat.id)">Usuń</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="3" class="text-center py-4">Brak kategorii</td></tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class CategoryListComponent {
  private service = inject(CategoryService);
  categories$ = this.service.getAll();

  onDelete(id: string) {
    if (confirm('Usunąć kategorię? Upewnij się, że nie ma przypisanych produktów.')) {
      this.service.delete(id).subscribe(() => {
        this.categories$ = this.service.getAll(); // Refresh
      });
    }
  }
}
