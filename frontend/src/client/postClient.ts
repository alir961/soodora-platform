import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Post } from '@/entity/Post';

type CreatePostRequest = Omit<Omit<Post, 'slug'>, 'owner'>;

async function createPost(post: CreatePostRequest): Promise<void> {
  return axios
    .post('/posts', post, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 201) return reject('Unable to call API');
    })
    .catch(reject);
}

async function retrievePosts(): Promise<Post[]> {
  return axios.get('/posts').then((response) => {
    if (response && response.status === 200 && response.data) return response.data as Post[];
    return reject('Unable to call API');
  });
}

async function deletePost(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

export type { CreatePostRequest };
export { createPost, retrievePosts, deletePost };
