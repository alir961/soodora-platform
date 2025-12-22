import axios from 'axios';
import { Playlist, PlaylistFull, PlaylistSlugResponse } from '@/entity/Playlist';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Pageable } from '@/entity/PageableParams';
import { PlaylistSearchParams, PlaylistSearchResponse } from '@/entity/PlaylistSearch';

export async function createPlaylist(request: FormData): Promise<PlaylistSlugResponse> {
  return axios
    .post(`/playlists`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 201 && response.data) return response.data;
      return reject('Unable to create playlist');
    })
    .catch(reject);
}

export const fetchPlaylistBySlug = async (slug: string, page: Pageable): Promise<PlaylistFull> => {
  return axios
    .get(`/playlists/${slug}`, {
      params: page,
      headers: await getAuthorizedHeader(),
    })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data;
      return reject('Unable to fetch playlist');
    })
    .catch(reject);
};

export const searchPlaylists = async (
  userSlug?: string,
  name?: string,
  description?: string,
  page = 0,
  size = 20,
  sort = 'createdAt,desc'
): Promise<PlaylistSearchResponse> => {
  const params: PlaylistSearchParams = { page, size, sort };
  if (userSlug) params.userSlug = userSlug;
  if (name) params.name = name;
  if (description) params.description = description;

  return axios
    .get(`/playlists/search`, {
      headers: await getAuthorizedHeader(),
      params,
    })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data;
      return reject('Unable to search playlists');
    })
    .catch(reject);
};

export const updatePlaylistBySlug = async (slug: string, formData: FormData): Promise<Playlist> => {
  return axios
    .put(`/playlists/${slug}`, formData, {
      headers: {
        ...(await getAuthorizedHeader()),
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data.playlistDTO;
      return reject('Unable to update playlist');
    })
    .catch(reject);
};

export const deletePlaylistBySlug = async (slug: string): Promise<void> => {
  return axios
    .delete(`/playlists/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200) return;
      return reject('Unable to delete playlist');
    })
    .catch(reject);
};

export const addPostToPlaylist = async (playlistSlug: string, postSlug: string): Promise<PlaylistFull> => {
  return axios
    .post(`/playlists/${playlistSlug}/posts/${postSlug}`, null, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data;
      return reject('Unable to add post to playlist');
    })
    .catch(reject);
};

export const removePostFromPlaylist = async (playlistSlug: string, postSlug: string): Promise<PlaylistFull> => {
  return axios
    .delete(`/playlists/${playlistSlug}/posts/${postSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data;
      return reject('Unable to remove post from playlist');
    })
    .catch(reject);
};
