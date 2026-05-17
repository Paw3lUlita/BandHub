-- V18__Ui_Dictionary_Orders.sql
-- Klucze UI dla statusow zamowien, koszyka, setlist na koncercie i sesji JWT.

INSERT INTO ui_dictionary (key_name, value, description, updated_at) VALUES
    ('order.status.NEW',       'Oczekuje na zaplate',              'Status zamowienia NEW',           NOW()),
    ('order.status.PAID',        'Oplacone',                         'Status zamowienia PAID',          NOW()),
    ('order.status.SHIPPED',     'Wyslane',                          'Status zamowienia SHIPPED',       NOW()),
    ('order.status.DELIVERED',   'Dostarczone',                      'Status zamowienia DELIVERED',     NOW()),
    ('order.status.CANCELLED',   'Anulowane',                        'Status zamowienia CANCELLED',     NOW()),
    ('product.cta.viewCart',     'Przejdz do koszyka',               'CTA po dodaniu do koszyka',       NOW()),
    ('product.cta.continue',     'Kontynuuj zakupy',                 'CTA kontynuuj zakupy',            NOW()),
    ('product.feedback.added',   'Dodano do koszyka',                'Komunikat po dodaniu',            NOW()),
    ('concert.section.setlist',  'Setlista koncertu',                'Naglowek sekcji setlist',         NOW()),
    ('concert.empty.setlist',    'Setlista jeszcze nie zostala opublikowana.', 'Empty state setlist', NOW()),
    ('concert.cta.viewMyTickets','Zobacz moje bilety',               'CTA po zakupie biletu',           NOW()),
    ('auth.session.expired',     'Twoja sesja wygasla. Zaloguj sie ponownie.', 'Banner wygaslej sesji', NOW())
ON CONFLICT (key_name) DO NOTHING;
