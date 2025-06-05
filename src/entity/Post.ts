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

interface PostLike {
  postSlug: string;
  user: User;
  likedAt: string;
}

interface PostCounter {
  likeCount: number;
  commentCount: number;
}

interface PostCounters {
  [slug: string]: PostCounter;
}

interface Comment {
  slug: string;
  postSlug: string;
  content: string;
  user: User;
  likeCount: number;
  createdAt: string;
}

export type { Post, PostLike, PostCounter, PostCounters, Comment };
