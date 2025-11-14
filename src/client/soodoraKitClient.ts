import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import {
  CreateSoodoraKitRequest,
  SoodoraKitDTO,
  SoodoraKitItemDTO,
  UpdateSoodoraKitRequest,
} from '@/entity/SoodoraKit';
import { reject } from '@/utils';

const BASE = '/media-kits';

export async function createSoodoraKit(request: CreateSoodoraKitRequest): Promise<SoodoraKitDTO> {
  return axios
    .post(BASE, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data?.mediaKitDTO) return response.data.mediaKitDTO;
      return reject('Failed to create SoodoraKit');
    })
    .catch(reject);
}

export async function getSoodoraKitBySlug(slug: string): Promise<SoodoraKitDTO> {
  return axios
    .get(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data;
      return reject('SoodoraKit not found');
    })
    .catch(reject);
}

export async function getSoodoraKitArtistSlug(artistSlug: string): Promise<SoodoraKitDTO> {
  return axios
    .get(`${BASE}/artist/${artistSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data;
      return reject('SoodoraKit not found for artist');
    })
    .catch(reject);
}

export async function updateSoodoraKit(slug: string, request: UpdateSoodoraKitRequest): Promise<SoodoraKitDTO> {
  return axios
    .put(`${BASE}/${slug}`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.mediaKitDTO) return response.data.mediaKitDTO;
      return reject('Failed to update SoodoraKit');
    })
    .catch(reject);
}

export async function deleteSoodoraKit(slug: string): Promise<void> {
  return axios
    .delete(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Failed to delete SoodoraKit');
    })
    .catch(reject);
}

export async function addSoodoraKitItem(artistSlug: string, request: FormData): Promise<SoodoraKitItemDTO> {
  return axios
    .post(`${BASE}/${artistSlug}/items`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data?.mediaKitItemDTO) return response.data.mediaKitItemDTO;
      return reject('Failed to add SoodoraKit item');
    })
    .catch(reject);
}

export async function updateSoodoraKitItem(itemSlug: string, request: FormData): Promise<SoodoraKitItemDTO> {
  return axios
    .put(`${BASE}/items/${itemSlug}`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.mediaKitItemDTO) return response.data.mediaKitItemDTO;
      return reject('Failed to update SoodoraKit item');
    })
    .catch(reject);
}

export async function deleteSoodoraKitItem(itemSlug: string): Promise<void> {
  return axios
    .delete(`${BASE}/items/${itemSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Failed to delete SoodoraKit item');
    })
    .catch(reject);
}

export async function exportSoodoraKitPdf(slug: string): Promise<Blob> {
  return axios
    .get(`${BASE}/${slug}/export/pdf`, { headers: await getAuthorizedHeader(), responseType: 'blob' })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as Blob;
      return reject('Failed to export PDF');
    })
    .catch(reject);
}
