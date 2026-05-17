import { Component } from '@angular/core';

interface GuideSection {
  title: string;
  intro: string;
  items: { name: string; hint: string }[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="max-w-4xl">
      <h2 class="text-2xl font-bold mb-2">Panel BandHub</h2>
      <p class="opacity-70 mb-8">
        Poniżej krótka instrukcja do sekcji z menu bocznego. Wybierz zakładkę po lewej, aby przejść do danej funkcji.
      </p>

      <div class="space-y-8">
        @for (section of sections; track section.title) {
          <section>
            <h3 class="text-lg font-semibold text-primary mb-1">{{ section.title }}</h3>
            <p class="text-sm opacity-70 mb-3">{{ section.intro }}</p>
            <ul class="list-disc list-inside space-y-1.5 text-sm">
              @for (item of section.items; track item.name) {
                <li>
                  <span class="font-medium">{{ item.name }}</span>
                  <span class="opacity-70"> — {{ item.hint }}</span>
                </li>
              }
            </ul>
          </section>
        }
      </div>
    </div>
  `
})
export class DashboardComponent {
  sections: GuideSection[] = [
    {
      title: 'E-commerce',
      intro: 'Zarządzanie sklepem merch w aplikacji mobilnej: katalog, zamówienia i obsługa płatności oraz wysyłek.',
      items: [
        { name: 'Merch (Produkty)', hint: 'Dodawaj i edytuj produkty, stany magazynowe oraz ceny.' },
        { name: 'Kategorie', hint: 'Grupuj produkty w kategorie widoczne w mobilce.' },
        { name: 'Zamówienia', hint: 'Przeglądaj zamówienia fanów; zmieniaj status (np. opłacone, wysłane).' },
        { name: 'Płatności / Transakcje', hint: 'Rejestr płatności powiązanych z zamówieniami.' },
        { name: 'Wysyłki', hint: 'Adres dostawy, przewoźnik i numer śledzenia.' },
        { name: 'Historia statusów', hint: 'Kto i kiedy zmienił status zamówienia merch (audyt).' }
      ]
    },
    {
      title: 'Ticketing & Trasa',
      intro: 'Koncerty, pule biletów, sprzedaż i kontrola wejścia na wydarzenie.',
      items: [
        { name: 'Koncerty', hint: 'Planuj wydarzenia, miejsca, pule biletów i ceny.' },
        { name: 'Miejsca', hint: 'Kluby i hale — adres, pojemność, kontakt.' },
        { name: 'Zamówienia biletów', hint: 'Zakupy fanów z aplikacji; filtruj po koncercie.' },
        { name: 'Pozycje zamówień / Kody / Walidacje / Zwroty', hint: 'Szczegóły techniczne biletów i skanowanie przy bramce.' },
        { name: 'Skan biletu', hint: 'Sprawdź kod QR przy wejściu na koncert.' }
      ]
    },
    {
      title: 'CMS / Treści',
      intro: 'Treści widoczne fanom w aplikacji: branding, aktualności i teksty interfejsu.',
      items: [
        { name: 'Ustawienia strony', hint: 'Nazwa zespołu, logo, kolory — wygląd Home w mobilce.' },
        { name: 'Słownik UI', hint: 'Etykiety i komunikaty w aplikacji (bez nowej wersji mobilnej).' },
        { name: 'Aktualności', hint: 'Artykuły na ekranie głównym; zdjęcie z galerii.' },
        { name: 'Galeria zdjęć', hint: 'Biblioteka zdjęć do aktualności i materiałów promocyjnych.' }
      ]
    },
    {
      title: 'Fan / Mobile',
      intro: 'Setlisty koncertów widoczne dla fanów w aplikacji mobilnej.',
      items: [
        { name: 'Setlisty', hint: 'Utwórz setlistę dla koncertu (dropdown koncertu + lista utworów); opublikuj datą, aby fan ją widział.' },
        { name: 'Pozycje setlist', hint: 'Podgląd wszystkich utworów we wszystkich setlistach.' }
      ]
    },
    {
      title: 'IAM (administratorzy)',
      intro: 'Konta w Keycloak — managerzy panelu, fani w aplikacji mobilnej, role i grupy.',
      items: [
        { name: 'Użytkownicy', hint: 'Tworzenie kont, reset hasła, włączanie/wyłączanie.' },
        { name: 'Role / Grupy', hint: 'Uprawnienia dostępu do panelu admina.' }
      ]
    },
    {
      title: 'Logistyka & Finanse',
      intro: 'Planowanie trasy koncertowej i rozliczenia kosztów oraz przychodów.',
      items: [
        { name: 'Trasy koncertowe', hint: 'Cała trasa z odcinkami, kosztami i przychodami.' },
        { name: 'Odcinki / Kategorie / Rozliczenia', hint: 'Szczegóły finansowe poszczególnych etapów trasy.' }
      ]
    },
    {
      title: 'Raportowanie',
      intro: 'Podsumowania sprzedaży i eksporty do analizy lub dokumentów.',
      items: [
        { name: 'Raport merchu / Raport wydarzenia (bilety)', hint: 'Sprzedaż i przychody dla wybranego zakresu.' },
        { name: 'Generator raportów / Szablony DOCX', hint: 'Raporty z szablonów Word.' },
        { name: 'Uruchomienia raportów / Zadania eksportu', hint: 'Historia wygenerowanych plików.' }
      ]
    }
  ];
}
