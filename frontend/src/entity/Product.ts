import { User } from '@/entity/User';

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
  vendor: User;
  createdAt: Date;
}

export type { Product, ProductSearchParams };
