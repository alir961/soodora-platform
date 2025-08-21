import type { User } from '@/entity/User';

interface UserPage {
  content: User[];
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}

export type { UserPage };
