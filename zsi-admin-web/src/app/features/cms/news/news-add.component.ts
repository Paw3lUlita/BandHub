import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CmsService, GalleryImage } from '../../../core/services/cms.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-news-add',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-3xl mx-auto">
      <h2 class="text-2xl font-bold mb-6">
        {{ isEditMode ? 'Edytuj Artykuł' : 'Napisz Nowy Artykuł' }}
      </h2>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Tytuł</span></label>
          <input type="text" formControlName="title" class="input input-bordered"
                 [class.input-error]="isInvalid('title')" />
        </div>

        <div class="form-control w-full mb-4">
          <label class="label"><span class="label-text">Zdjęcie główne (z Galerii)</span></label>

          <select formControlName="imageUrl" class="select select-bordered"
                  [class.select-error]="isInvalid('imageUrl')">
            <option value="" disabled selected>Wybierz zdjęcie...</option>
            @for (img of galleryImages$ | async; track img.id) {
              <option [value]="img.imageUrl">{{ img.title }}</option>
            }
          </select>

          @if (form.get('imageUrl')?.value) {
            <div class="mt-2">
              <img [src]="form.get('imageUrl')?.value" class="h-32 rounded-lg shadow-sm" alt="Podgląd">
            </div>
          }
        </div>

        <div class="form-control w-full mb-6">
          <label class="label"><span class="label-text">Treść artykułu</span></label>
          <textarea formControlName="content" class="textarea textarea-bordered h-64 text-base leading-relaxed"></textarea>
          @if (isInvalid('content')) {
            <span class="text-error text-xs mt-1">Treść jest wymagana</span>
          }
        </div>

        <div class="flex justify-end gap-4">
          <a routerLink="/admin/news" class="btn btn-ghost">Anuluj</a>
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isSubmitting">
            @if(isSubmitting) { <span class="loading loading-spinner"></span> }
            {{ isEditMode ? 'Zapisz Zmiany' : 'Opublikuj' }}
          </button>
        </div>

      </form>
    </div>
  `
})
export class NewsAddComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cmsService = inject(CmsService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isSubmitting = false;
  isEditMode = false;
  newsId: string | null = null;
  galleryImages$ = this.cmsService.getImages(); // Pobieramy galerię do selecta

  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(5)]],
    content: ['', Validators.required],
    imageUrl: ['', Validators.required]
  });

  ngOnInit() {
    this.newsId = this.route.snapshot.paramMap.get('id');
    if (this.newsId) {
      this.isEditMode = true;
      this.cmsService.getNews(this.newsId).subscribe(news => {
        this.form.patchValue({
          title: news.title,
          content: news.content,
          imageUrl: news.imageUrl
        });
      });
    }
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.isSubmitting = true;

    const request = {
      title: this.form.value.title!,
      content: this.form.value.content!,
      imageUrl: this.form.value.imageUrl!
    };

    const operation = this.isEditMode && this.newsId
      ? this.cmsService.updateNews(this.newsId, request)
      : this.cmsService.createNews(request);

    operation.subscribe({
      next: () => this.router.navigate(['/admin/news']),
      error: (err) => {
        console.error(err);
        this.isSubmitting = false;
      }
    });
  }
}
