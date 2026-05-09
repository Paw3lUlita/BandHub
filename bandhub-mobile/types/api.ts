export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  sortBy: string;
  sortDir: string;
  query: string;
};

export type News = {
  id: string;
  title: string;
  content: string;
  imageUrl: string | null;
  publishedDate: string;
  authorId: string | null;
};

export type GalleryImage = {
  id: string;
  title: string;
  imageUrl: string;
  uploadedAt: string;
};

export type Product = {
  id: string;
  name: string;
  description: string;
  price: number;
  currency: string;
  stockQuantity: number;
  categoryName: string | null;
  categoryId: string | null;
};

export type Concert = {
  id: string;
  name: string;
  date: string;
  venueName: string;
  city: string;
};

export type TicketPool = {
  id: string;
  name: string;
  price: number;
  currency: string;
  totalQuantity: number;
  remainingQuantity: number;
};

export type ConcertDetail = {
  id: string;
  name: string;
  date: string;
  description: string | null;
  imageUrl: string | null;
  venueName: string | null;
  venueCity: string | null;
  ticketPools: TicketPool[];
};

export type Setlist = {
  id: string;
  concertId: string;
  concertName: string;
  title: string;
  createdBy: string | null;
  publishedAt: string | null;
  createdAt: string;
};

export type SetlistItem = {
  id: string;
  setlistId: string;
  songTitle: string;
  songOrder: number;
  durationSeconds: number | null;
};

export type TicketPurchaseResponse = {
  orderId: string;
  ticketCodes: string[];
};

export type LocalTicketPurchase = {
  id: string;
  orderId: string;
  concertId: string;
  concertName: string;
  purchasedAt: string;
  ticketCodes: string[];
};

export type CartItem = {
  productId: string;
  name: string;
  price: number;
  currency: string;
  quantity: number;
};

export type LocalMerchOrder = {
  id: string;
  orderRef: string;
  createdAt: string;
  totalAmount: number;
  currency: string;
  items: CartItem[];
  deliveryAddress?: string;
  paymentProvider?: string;
};

export type SiteSettings = {
  bandName: string;
  tagline: string | null;
  heroImageUrl: string | null;
  aboutText: string | null;
  updatedAt: string | null;
  updatedBy: string | null;
};
