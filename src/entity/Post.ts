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
  likeCount: number;
  repostCount: number;
  commentCount: number;
}

interface PostLike {
  postSlug: string;
  user: User;
  likedAt: string;
}

interface Comment {
  slug: string;
  postSlug: string;
  content: string;
  user: User;
  likeCount: number;
  createdAt: string;
}

export type { Post, PostLike, Comment };
