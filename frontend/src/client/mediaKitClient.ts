import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { CreateMediaKitRequest, MediaKitDTO, MediaKitItemDTO, UpdateMediaKitRequest } from '@/entity/MediaKit';
import { reject } from '@/utils';

const BASE = '/mediakits';

export async function createMediaKit(request: CreateMediaKitRequest): Promise<MediaKitDTO> {
  return axios
    .post(BASE, request, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 201 && res.data?.mediaKitDTO) return res.data.mediaKitDTO as MediaKitDTO;
      return reject('Failed to create MediaKit');
    })
    .catch(reject);
}

export async function getMediaKitBySlug(slug: string): Promise<MediaKitDTO> {
  return axios
    .get(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 200 && res.data) return res.data as MediaKitDTO;
      return reject('MediaKit not found');
    })
    .catch(reject);
}

export async function getMediaKitByArtistSlug(artistSlug: string): Promise<MediaKitDTO> {
  return axios
    .get(`${BASE}/artist/${artistSlug}`, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 200 && res.data) return res.data as MediaKitDTO;
      return reject('MediaKit not found for artist');
    })
    .catch(reject);
}

export async function updateMediaKit(slug: string, request: UpdateMediaKitRequest): Promise<MediaKitDTO> {
  return axios
    .put(`${BASE}/${slug}`, request, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 202 && res.data?.mediaKitDTO) return res.data.mediaKitDTO as MediaKitDTO;
      return reject('Failed to update MediaKit');
    })
    .catch(reject);
}

export async function deleteMediaKit(slug: string): Promise<void> {
  return axios
    .delete(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 200) return;
      return reject('Failed to delete MediaKit');
    })
    .catch(reject);
}

export async function addMediaKitItem(artistSlug: string, request: FormData): Promise<MediaKitItemDTO> {
  return axios
    .post(`${BASE}/${artistSlug}/items`, request, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 201 && res.data?.mediaKitItemDTO) return res.data as MediaKitItemDTO;
      return reject('Failed to add MediaKit item');
    })
    .catch(reject);
}

export async function updateMediaKitItem(itemSlug: string, request: FormData): Promise<MediaKitItemDTO> {
  return axios
    .put(`${BASE}/items/${itemSlug}`, request, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 202 && res.data?.mediaKitItemDTO) return res.data as MediaKitItemDTO;
      return reject('Failed to update MediaKit item');
    })
    .catch(reject);
}

export async function deleteMediaKitItem(itemSlug: string): Promise<void> {
  return axios
    .delete(`${BASE}/items/${itemSlug}`, { headers: await getAuthorizedHeader() })
    .then((res) => {
      if (res && res.status === 200) return;
      return reject('Failed to delete MediaKit item');
    })
    .catch(reject);
}

export async function exportMediaKitPdf(slug: string): Promise<Blob> {
  return axios
    .get(`${BASE}/${slug}/export/pdf`, { headers: await getAuthorizedHeader(), responseType: 'blob' })
    .then((res) => {
      if (res && res.status === 200 && res.data) return res.data as Blob;
      return reject('Failed to export PDF');
    })
    .catch(reject);
}
