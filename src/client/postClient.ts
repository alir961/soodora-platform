import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Post } from '@/entity/Post';

type CreatePostRequest = Omit<Omit<Post, 'slug'>, 'owner'>;

async function createPost(post: CreatePostRequest): Promise<void> {
  return axios.post('/posts', post, { headers: await getAuthorizedHeader() });
}

async function retrievePosts(): Promise<Post[]> {
  return axios.get('/posts').then((response) => {
    if (response && response.status === 200 && response.data) return response.data as Post[];
    return reject('Unable to call API');
  });
}

export type { CreatePostRequest };
export { createPost, retrievePosts };
