import { Artist } from '@/entity/Artist';

interface ProductSearchParams {
  name?: string;
  description?: string;
  category?: string;
  userSlug?: string;
  page: number;
  size: number;
  sort: string;
}

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

export type { Product, ProductSearchParams };
