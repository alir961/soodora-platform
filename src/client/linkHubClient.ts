import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { LinkHub } from '@/entity/LinkHub';
import { Link } from '@/entity/Link';
import { reject } from '@/utils';

export type CreateLinkHubRequest = Omit<Omit<LinkHub, 'slug'>, 'owner' | 'links'>;
export type UpdateLinkHubRequest = Partial<CreateLinkHubRequest>;

export type CreateLinkRequest = Omit<Link, 'slug'>;
export type UpdateLinkRequest = Partial<CreateLinkRequest>;

export async function createLinkHub(command: CreateLinkHubRequest): Promise<string> {
  return axios
    .post('/link-hubs', command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data) return response.data.slug;
      return reject('Unable to create link page');
    })
    .catch(reject);
}

export async function getLinkHub(slug: string): Promise<LinkHub> {
  return axios
    .get(`/link-hubs/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkHub;
      return reject('Unable to retrieve link page');
    })
    .catch(reject);
}

export async function getUserLinkHub(userSlug: string): Promise<LinkHub> {
  return axios
    .get('/link-hubs', { headers: await getAuthorizedHeader(), params: { userSlug } })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkHub;
      return reject('Not found link page');
    })
    .catch(reject);
}

export async function updateLinkHub(slug: string, command: UpdateLinkHubRequest) {
  return axios
    .put(`/link-hubs/${slug}`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Unable to update link page');
    })
    .catch(reject);
}

export async function deleteLinkHub(slug: string): Promise<void> {
  return axios
    .delete(`/link-hubs/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link page');
    })
    .catch(reject);
}

export async function createLink(linkHubSlug: string, command: CreateLinkRequest): Promise<string> {
  return axios
    .post(`/link-hubs/${linkHubSlug}/links`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data) return response.data.slug;
      return reject('Unable to create link');
    })
    .catch(reject);
}

export async function updateLink(linkHubSlug: string, linkSlug: string, command: UpdateLinkRequest) {
  return axios
    .put(`/link-hubs/${linkHubSlug}/links/${linkSlug}`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Unable to update link');
    })
    .catch(reject);
}

export async function deleteLink(linkHubSlug: string, linkSlug: string): Promise<void> {
  return axios
    .delete(`/link-hubs/${linkHubSlug}/links/${linkSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link');
    })
    .catch(reject);
}
