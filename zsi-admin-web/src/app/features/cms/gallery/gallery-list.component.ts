import { Component, inject } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CmsService } from '../../../core/services/cms.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-gallery-list',
  standalone: true,
  imports: [AsyncPipe, DatePipe, FormsModule],
  template: `
    <div class="p-4">
      <h2 class="text-2xl font-bold mb-6">Galeria Zdjęć</h2>

      <div class="card bg-base-100 shadow-xl mb-8 border border-base-200">
        <div class="card-body">
          <h3 class="card-title text-sm uppercase text-gray-500">Dodaj nowe zdjęcie</h3>

          <div class="flex flex-col md:flex-row gap-4 items-end">
            <div class="form-control w-full md:w-1/3">
              <label class="label"><span class="label-text">Tytuł zdjęcia</span></label>
              <input type="text" [(ngModel)]="uploadTitle" placeholder="np. Koncert Kraków" class="input input-bordered w-full" />
            </div>

            <div class="form-control w-full md:w-1/3">
              <label class="label"><span class="label-text">Plik</span></label>
              <input type="file" (change)="onFileSelected($event)" class="file-input file-input-bordered w-full" accept="image/*" />
            </div>

            <button class="btn btn-primary"
              [disabled]="!selectedFile || !uploadTitle || isUploading"
              (click)="upload()">
              @if(isUploading) { <span class="loading loading-spinner"></span> }
              Wyślij na serwer
            </button>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
        @for (img of images$ | async; track img.id) {
          <div class="card bg-base-100 shadow-sm hover:shadow-md transition-shadow">
            <figure class="h-40 relative group">
              <img [src]="img.imageUrl" [alt]="img.title" class="object-cover w-full h-full" />
              <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                <button class="btn btn-error btn-sm btn-circle" (click)="delete(img.id)">🗑️</button>
              </div>
            </figure>
            <div class="card-body p-3">
              <h4 class="font-bold text-xs truncate">{{ img.title }}</h4>
              <p class="text-[10px] text-gray-400">{{ img.uploadedAt | date:'shortDate' }}</p>
            </div>
          </div>
        } @empty {
          <div class="col-span-full text-center py-10 text-gray-500">
            Brak zdjęć. Dodaj pierwsze zdjęcie powyżej.
          </div>
        }
      </div>
    </div>
  `
})
export class GalleryListComponent {
  private cmsService = inject(CmsService);

  // Strumień danych
  images$ = this.cmsService.getImages();

  // Stan formularza lokalnego
  uploadTitle = '';
  selectedFile: File | null = null;
  isUploading = false;

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  upload() {
    if (!this.selectedFile || !this.uploadTitle) return;

    this.isUploading = true;
    this.cmsService.uploadImage(this.uploadTitle, this.selectedFile).subscribe({
      next: () => {
        alert('Zdjęcie dodane!');
        this.resetForm();
        // Odświeżamy listę przypisując strumień na nowo
        this.images$ = this.cmsService.getImages();
      },
      error: (err) => {
        console.error(err);
        this.isUploading = false;
        alert('Błąd wysyłania pliku');
      }
    });
  }

  delete(id: string) {
    if (confirm('Usunąć to zdjęcie na zawsze?')) {
      this.cmsService.deleteImage(id).subscribe(() => {
        this.images$ = this.cmsService.getImages();
      });
    }
  }

  private resetForm() {
    this.isUploading = false;
    this.uploadTitle = '';
    this.selectedFile = null;
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }
}
