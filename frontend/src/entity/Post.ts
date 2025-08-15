import { User } from '@/entity/User';

interface Post {
  slug: string;
  title: string;
  content: string;
  imageUrl: string;
  audioUrl: string;
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
  createdAt: Date;
}

function populatePost(post: Post): Post {
  return {
    ...post,
    createdAt: new Date(post.createdAt),
  } as Post;
}

export { populatePost };
export type { Post, PostLike, Comment };
