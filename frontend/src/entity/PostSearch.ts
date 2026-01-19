import { Post } from '@/entity/Post';

interface PostSearchParams {
  title?: string;
  audioTitle?: string;
  description?: string;
  hasAudio?: boolean;
  page: number;
  size: number;
  sort: string;
}

interface PostSearchResponse {
  content: Post[];
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}

export type { PostSearchResponse, PostSearchParams };
