import { Playlist } from '@/entity/Playlist';

export interface PlaylistSearchParams {
  userSlug?: string;
  name?: string;
  description?: string;
  privacy?: string;
  page: number;
  size: number;
  sort: string;
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
