import { User } from '@/entity/User';
import { Artist } from '@/entity/Artist';

interface Post {
  slug: string;
  title: string;
  content: string;
  imageUrl: string;
  audioTitle: string;
  audioUrl: string;
  owner: Artist;
  createdAt: Date;
  likeCount: number;
  repostCount: number;
  commentCount: number;
}

interface Comment {
  slug: string;
  postSlug: string;
  content: string;
  user: User;
  likeCount: number;
  createdAt: Date;
}

interface PostAnalytics {
  likes: number;
  comments: number;
  reposts: number;
  subscribedLikes: number;
  subscribedComments: number;
  subscribedReposts: number;
}

function populatePost(post: Post): Post {
  return {
    ...post,
    createdAt: new Date(post.createdAt),
  } as Post;
}

export { populatePost };
export type { Post, Comment, PostAnalytics };
