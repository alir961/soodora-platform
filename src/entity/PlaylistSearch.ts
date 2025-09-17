import { Playlist } from '@/entity/Playlist';

export interface PlaylistSearchParams {
  userSlug?: string;
  title?: string;
  description?: string;
  page: number;
  size: number;
}

export interface PlaylistSearchResponse {
  content: Playlist[];
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}
