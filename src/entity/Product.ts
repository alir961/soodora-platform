import { Artist } from '@/entity/Artist';
import { Money } from '@/entity/Money';

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
  currency: string;
  quantity: number;
}

export type { Product, ProductCreationData };
