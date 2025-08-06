import { User } from '@/entity/User';
import { Product } from '@/entity/Product';

interface Sale {
  slug: string;
  product: Product;
  user: User;
  quantitySold: number;
  amount: number;
  createdAt: string;
}

export type { Sale };
