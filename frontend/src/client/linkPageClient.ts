import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { LinkPage } from '@/entity/LinkPage';
import { Link } from '@/entity/Link';
import { reject } from '@/utils';

export type CreateLinkPageRequest = Omit<Omit<LinkPage, 'slug'>, 'owner' | 'links'>;
export type UpdateLinkPageRequest = Partial<CreateLinkPageRequest>;

export type CreateLinkRequest = Omit<Link, 'slug'>;
export type UpdateLinkRequest = Partial<CreateLinkRequest>;

export async function createLinkPage(command: CreateLinkPageRequest): Promise<LinkPage> {
  return axios
    .post('/linkpages', command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data) return response.data as LinkPage;
      return reject('Unable to create link page');
    })
    .catch(reject);
}

export async function getLinkPage(slug: string): Promise<LinkPage> {
  return axios
    .get(`/linkpages/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkPage;
      return reject('Unable to retrieve link page');
    })
    .catch(reject);
}

export async function getUserLinkPage(userSlug: string): Promise<LinkPage> {
  return axios
    .get('/linkpages', { headers: await getAuthorizedHeader(), params: { userSlug } })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkPage;
      return reject('Not found link page');
    })
    .catch(reject);
}

export async function updateLinkPage(slug: string, command: UpdateLinkPageRequest) {
  return axios
    .put(`/linkpages/${slug}`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 202) return;
      return reject('Unable to update link page');
    })
    .catch(reject);
}

export async function deleteLinkPage(slug: string): Promise<void> {
  return axios
    .delete(`/linkpages/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link page');
    })
    .catch(reject);
}

export async function createLink(linkPageSlug: string, command: CreateLinkRequest): Promise<Link> {
  return axios
    .post(`/linkpages/${linkPageSlug}/links`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data) return response.data as Link;
      return reject('Unable to create link');
    })
    .catch(reject);
}

export async function updateLink(linkPageSlug: string, linkSlug: string, command: UpdateLinkRequest) {
  return axios
    .put(`/linkpages/${linkPageSlug}/links/${linkSlug}`, command, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 202) return;
      return reject('Unable to update link');
    })
    .catch(reject);
}

export async function deleteLink(linkPageSlug: string, linkSlug: string): Promise<void> {
  return axios
    .delete(`/linkpages/${linkPageSlug}/links/${linkSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link');
    })
    .catch(reject);
}
