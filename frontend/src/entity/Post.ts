import { User } from '@/entity/User';

interface Post {
  slug: string;
  title: string;
  content: string;
  imageUrl: string;
  audioUrl: string;
  type: 'ANNOUNCE' | 'MERCH_DROP' | 'NEW_EVENT';
  owner: User;
  createdAt: Date;
}

export type { Post };
