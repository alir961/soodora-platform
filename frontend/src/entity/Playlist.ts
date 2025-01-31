import { User } from '@/entity/User';

type PlaylistPrivacy = 'PUBLIC' | 'PRIVATE' | 'UNLISTED';

interface Playlist {
  slug: string;
  name: string;
  description: string;
  privacy: PlaylistPrivacy;
  imageUrl: string;
  numberOfTracks: number;
  owner: User;
}

export type { PlaylistPrivacy, Playlist };
