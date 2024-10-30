import {User} from "@/entity/User";

type PlaylistPrivacy = 'PUBLIC' | 'PRIVATE' | 'UNLISTED';

interface Playlist {
    slug: string;
    name: string;
    description: string;
    privacy: PlaylistPrivacy;
    imageUrl: string;
    owner: User;
}

export type {PlaylistPrivacy, Playlist}