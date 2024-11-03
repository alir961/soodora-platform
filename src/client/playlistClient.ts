import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { Playlist } from '@/entity/Playlist';
import { reject } from '@/utils';

type PlaylistCreateRequest = Omit<Omit<Playlist, 'slug'>, 'owner'>;

async function createPlaylist(playlist: PlaylistCreateRequest): Promise<void> {
  return axios.post(`/playlists`, playlist, { headers: await getAuthorizedHeader() });
}

async function retrieveUserPlaylists(userSlug: string): Promise<Playlist[]> {
  return axios.get(`/playlists?userSlug=${userSlug}`, { headers: await getAuthorizedHeader() }).then((response) => {
    if (response && response.status === 200 && response.data) return response.data as Playlist[];
    return reject('Unable to call API');
  });
}

async function retrievePlaylistBySlug(slug: string): Promise<Playlist> {
  return axios.get(`/playlists/${slug}`, { headers: await getAuthorizedHeader() }).then((response) => {
    if (response && response.status === 200 && response.data) return response.data as Playlist;
    return reject('Unable to call API');
  });
}

async function updatePlaylist(slug: string, playlist: Playlist): Promise<void> {
  return axios.put(`/playlists/${slug}`, playlist, { headers: await getAuthorizedHeader() });
}

export type { PlaylistCreateRequest };
export { createPlaylist, retrieveUserPlaylists, retrievePlaylistBySlug, updatePlaylist };
