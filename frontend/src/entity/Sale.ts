import { User } from '@/entity/User';
import { Product } from '@/entity/Product';
import { Artist } from '@/entity/Artist';
import { Money } from '@/entity/Money';

interface Sale {
  slug: string;
  product: Product;
  user: Artist;
  buyer: User;
  quantitySold: number;
  amount: Money;
  createdAt: string;
}

export type { Sale };
