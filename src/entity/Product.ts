import { Artist } from '@/entity/Artist';
import { Currency, Money } from '@/entity/Money';

interface Product {
  slug: string;
  name: string;
  description: string;
  imageUrl: string;
  category: string;
  price: Money;
  quantity: number | null;
  vendor: Artist;
  createdAt: Date;
}

interface ProductCreationData {
  name: string;
  description: string;
  category: string;
  price: number;
  currency: Currency;
  quantity: number;
}

interface ProductAnalytics {
  currentQuantity: number;
  soldQuantity: number;
  totalRevenue: number;
  salesCount: number;
  durationUntilFirstSale: number;
  currency: string;
}

export type { Product, ProductCreationData, ProductAnalytics };
