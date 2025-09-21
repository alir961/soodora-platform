import { Artist } from '@/entity/Artist';

interface Product {
  slug: string;
  name: string;
  description: string;
  imageUrl: string;
  category: string;
  price: number | null;
  quantity: number | null;
  vendor: Artist;
  createdAt: Date;
}

interface ProductCreationData {
  name: string;
  description: string;
  category: string;
  price: number;
  quantity: number;
}

export type { Product, ProductCreationData };
