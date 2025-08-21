import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { LinkPage } from '@/entity/LinkPage';
import { reject } from '@/utils';

export async function callFindLinkPage(slug: string): Promise<LinkPage> {
  return axios
    .get(`/linkpages/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkPage;
      return reject('Unable to retrieve link page');
    })
    .catch(reject);
}

export async function callFindUserLinkPage(userSlug: string): Promise<LinkPage> {
  return axios
    .get('/linkpages', { headers: await getAuthorizedHeader(), params: { userSlug } })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as LinkPage;
      return reject('Not found link page');
    })
    .catch(reject);
}

export async function callDeleteLinkPage(slug: string): Promise<void> {
  return axios
    .delete(`/linkpages/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link page');
    })
    .catch(reject);
}

export async function callDeleteLink(linkPageSlug: string, linkSlug: string): Promise<void> {
  return axios
    .delete(`/linkpages/${linkPageSlug}/links/${linkSlug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to delete link');
    })
    .catch(reject);
}
