import { User } from '@/entity/User';

interface Product {
  slug: string;
  name: string;
  description: string;
  imageUrl: string;
  category: string;
  price: number | null;
  quantity: number | null;
  vendor: User;
}

export type { Product };
