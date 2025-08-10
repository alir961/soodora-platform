import { User } from '@/entity/User';
import { Product } from '@/entity/Product';
import { Artist } from '@/entity/Artist';

interface Sale {
  slug: string;
  product: Product;
  user: Artist;
  buyer: User;
  quantitySold: number;
  amount: number;
  createdAt: string;
}

export type { Sale };
