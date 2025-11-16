import { Post } from './Post';
import { User } from './User';

export interface Playlist {
  slug: string;
  name: string;
  description: string;
  numberOfTracks: number;
  privacy: PlaylistPrivacy;
  owner: User;
  imageUrl: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface PlaylistFull {
  playlist: Playlist;
  posts: {
    content: Post[];
    page: {
      size: number;
      number: number;
      totalElements: number;
      totalPages: number;
    };
  };
}

export interface PlaylistSlugResponse {
  slug: string;
}

export type PlaylistPrivacy = 'PUBLIC' | 'PRIVATE' | 'UNLISTED';

export const LIKED_AUDIO_PLAYLIST_NAME = 'Liked Playlist';
