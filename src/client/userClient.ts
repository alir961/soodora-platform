import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { User } from '@/entity/User';
import { populatePost, Post } from '@/entity/Post';
import { Pageable } from '@/entity/PageableParams';
import { PostPage } from '@/client/postClient';
import { UserPage } from '@/entity/ProfileSearch';

async function callRetrieveUser(slug: string): Promise<User> {
  return axios
    .get(`/users/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
      return response.data as User;
    })
    .catch(reject);
}

async function callSearchUsers(
  username: string,
  onlyArtists: boolean,
  pageNumber: number,
  pageSize: number,
  sortValue: string
): Promise<UserPage> {
  try {
    const response = await axios.get<UserPage>('/users/search', {
      params: {
        username: encodeURIComponent(username),
        onlyArtists: onlyArtists,
        page: pageNumber,
        size: pageSize,
        sort: sortValue,
      },
    });

    return response.data;
  } catch (error) {
    let message: string = 'Fetch users from API failed!';

    if (error instanceof Error) message = error.message;
    return reject(message);
  }
}

async function callRetrieveFollowers(user: User): Promise<User[]> {
  return axios
    .get(`/users/${user.slug}/followers`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as User[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function callRetrieveFollowing(user: User): Promise<User[]> {
  return axios
    .get(`/users/${user.slug}/following`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as User[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function callRetrieveUserPosts(user: User, pageable: Pageable): Promise<PostPage> {
  return axios
    .get(`/users/${user.slug}/posts`, { params: pageable, headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status !== 200 || !response.data) return reject('Unable to call API');
      if (!response.data.content) return reject('Unable to parse API response');
      const content = response.data.content.map((post: Post) => populatePost(post));
      const hasMore = !response.data.empty && response.data.content.length === pageable.size;

      return { content, hasMore };
    })
    .catch(reject);
}

async function callRetrieveUserReposts(user: User, pageable: Pageable): Promise<PostPage> {
  return axios
    .get(`/users/${user.slug}/reposts`, { params: pageable, headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status !== 200 || !response.data) return reject('Unable to call API');
      if (!response.data.content) return reject('Unable to parse API response');
      const content = response.data.content.map((post: Post) => populatePost(post));
      const hasMore = !response.data.empty && response.data.content.length === pageable.size;

      return { content, hasMore };
    })
    .catch(reject);
}

async function callRetrieveUserAudios(user: User, pageable: Pageable): Promise<PostPage> {
  return axios
    .get(`/users/${user.slug}/audios`, { params: pageable, headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status !== 200 || !response.data) return reject('Unable to call API');
      if (!response.data.content) return reject('Unable to parse API response');
      const content = response.data.content.map((post: Post) => populatePost(post));
      const hasMore = !response.data.empty && response.data.content.length === pageable.size;

      return { content, hasMore };
    })
    .catch(reject);
}

export {
  callRetrieveUser,
  callSearchUsers,
  callRetrieveFollowers,
  callRetrieveFollowing,
  callRetrieveUserPosts,
  callRetrieveUserReposts,
  callRetrieveUserAudios,
};
