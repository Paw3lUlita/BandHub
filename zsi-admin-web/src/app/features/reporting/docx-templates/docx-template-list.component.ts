import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DOCX_MODULE_TOUR_SETTLEMENT, DocxTemplate, DocxTemplateService } from '../../../core/services/docx-template.service';

@Component({
  selector: 'app-docx-template-list',
  standalone: true,
  imports: [FormsModule, DatePipe],
  template: `
    <div class="max-w-5xl space-y-6">
      <h2 class="bh-page-title">Szablony DOCX (wydruki)</h2>
      <p class="text-sm text-base-content/70">
        Wgraj plik Word (.docx) z placeholderami w stylu Word (np. <code class="text-xs">&#36;&#123;tourName&#125;</code>).
        Moduł <strong>TOUR_SETTLEMENT</strong> — rozliczenie trasy. Pierwszy szablon jest aktywowany automatycznie.
      </p>

      <div class="card bg-base-200 shadow">
        <div class="card-body gap-3">
          <h3 class="card-title text-base">Nowy szablon</h3>
          <input
            type="text"
            class="input input-bordered input-sm w-full max-w-md"
            [(ngModel)]="uploadName"
            placeholder="Nazwa (np. Rozliczenie trasy v2)"
          />
          <input
            type="file"
            class="file-input file-input-bordered file-input-sm w-full max-w-md"
            accept=".docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            (change)="onFile($event)"
          />
          <button
            class="btn btn-primary btn-sm w-fit"
            [disabled]="!uploadName || !selectedFile || uploading()"
            (click)="upload()"
          >
            @if (uploading()) {
              <span class="loading loading-spinner loading-xs"></span>
            } @else {
              Wgraj
            }
          </button>
        </div>
      </div>

      <table class="table table-zebra bg-base-100 shadow rounded-box">
        <thead>
          <tr class="bg-base-200">
            <th>Nazwa</th>
            <th>Moduł</th>
            <th>Wersja</th>
            <th>Aktywny</th>
            <th>Utworzono</th>
            <th class="w-48">Akcje</th>
          </tr>
        </thead>
        <tbody>
          @for (t of items(); track t.id) {
            <tr>
              <td class="font-medium">{{ t.name }}</td>
              <td><span class="badge badge-ghost badge-sm">{{ t.moduleCode }}</span></td>
              <td>{{ t.templateVersion }}</td>
              <td>
                @if (t.active) {
                  <span class="badge badge-success badge-sm">tak</span>
                } @else {
                  <span class="badge badge-ghost badge-sm">nie</span>
                }
              </td>
              <td class="text-sm">{{ t.createdAt | date: 'short' }}</td>
              <td class="flex flex-wrap gap-1">
                @if (!t.active) {
                  <button class="btn btn-xs btn-primary" (click)="activate(t)">Aktywuj</button>
                }
                <button
                  class="btn btn-xs btn-ghost text-error"
                  [disabled]="t.active"
                  (click)="remove(t)"
                  title="Najpierw aktywuj inny szablon"
                >
                  Usuń
                </button>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="text-center py-6">Brak szablonów — wgraj pierwszy plik .docx.</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `
})
export class DocxTemplateListComponent implements OnInit {
  private service = inject(DocxTemplateService);

  items = signal<DocxTemplate[]>([]);
  uploadName = '';
  selectedFile: File | null = null;
  uploading = signal(false);

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.service.list(DOCX_MODULE_TOUR_SETTLEMENT).subscribe((list) => this.items.set(list));
  }

  onFile(ev: Event) {
    const input = ev.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  upload() {
    if (!this.uploadName || !this.selectedFile) return;
    this.uploading.set(true);
    this.service.upload(this.uploadName, DOCX_MODULE_TOUR_SETTLEMENT, this.selectedFile).subscribe({
      next: () => {
        this.uploadName = '';
        this.selectedFile = null;
        this.uploading.set(false);
        this.reload();
        alert('Szablon zapisany.');
      },
      error: () => this.uploading.set(false)
    });
  }

  activate(t: DocxTemplate) {
    this.service.activate(t.id).subscribe(() => this.reload());
  }

  remove(t: DocxTemplate) {
    if (!confirm('Usunąć szablon z dysku i bazy?')) return;
    this.service.delete(t.id).subscribe(() => this.reload());
  }
}
