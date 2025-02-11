import { User } from '@/entity/User';

interface Post {
  slug: string;
  title: string;
  content: string;
  owner: User;
}

export type { Post };
