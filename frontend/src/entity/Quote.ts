import { Money, Currency } from '@/entity/Money';
import { Artist } from '@/entity/Artist';

export interface QuoteDTO {
  slug: string;
  artist: Artist;
  name: string;
  clientName: string;
  clientAddress: string;
  clientEmail: string;
  clientPhone: string;
  description: string;
  quoteArticles: QuoteArticleDTO[];
  total: Money;
  createdAtDate: Date;
  updatedAt: Date;
}

export interface QuoteArticleDTO {
  quoteSlug: string;
  slug: string;
  description: string;
  quantity: number;
  unitPrice: Money;
  totalPrice: Money;
  vatPercent: number;
}

export interface CreateQuoteRequest {
  name: string;
  clientName: string;
  clientEmail: string;
  clientAddress: string;
  clientPhone: string;
  description: string;
}

export interface UpdateQuoteRequest {
  name?: string;
  clientName?: string;
  clientEmail?: string;
  clientAddress?: string;
  clientPhone?: string;
  description?: string;
}

export interface CreateQuoteArticleRequest {
  quoteSlug: string;
  description: string;
  quantity: number;
  price: number;
  currency: Currency;
  vatPercent: number;
}

export interface UpdateQuoteArticleRequest {
  quoteSlug?: string;
  description?: string;
  quantity?: number;
  price?: number;
  currency?: Currency;
  vatPercent?: number;
}

export interface QuoteSearchParams {
  clientName?: string;
  name?: string;
  description?: string;
  page: number;
  size: number;
  sort: string;
}

export interface QuotePage {
  content: QuoteDTO[];
  hasMore: boolean;
}
