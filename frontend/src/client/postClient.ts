import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Post, PostLike, Comment } from '@/entity/Post';

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
  try {
    const response = await axios.get('/posts');
    if (response.status === 200 && response.data) {
      return response.data.map((post: Post) => ({
        ...post,
        createdAt: new Date(post.createdAt),
      }));
    }
    return reject('Unable to call API');
  } catch (error: unknown) {
    let message: string = 'Post retrieving Failed!';
    if (error instanceof Error) message = error.message;

    return reject(message);
  }
}

async function deletePost(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

const postLike = async (slug: string): Promise<void> => {
  return axios
    .post(`/posts/${slug}/likes`, null, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to like post.');
    })
    .catch(reject);
};

const deleteLike = async (slug: string): Promise<void> => {
  return axios
    .delete(`/posts/${slug}/likes`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to remove like.');
    })
    .catch(reject);
};

const getPostCounters = async (slug: string): Promise<{ likeCount: number; commentCount: number }> => {
  const response = await axios.get(`/posts/${slug}/counters`);
  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve post counters.');
};

const getPostLikes = async (slug: string): Promise<PostLike[]> => {
  const response = await axios.get(`/posts/${slug}/likes`);
  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve post likes.');
};

export const getComments = async (postSlug: string): Promise<Comment[]> => {
  const response = await axios.get(`/posts/${postSlug}/comments`);
  if (response.status === 200 && response.data) return response.data;
  return Promise.reject('Failed to retrieve comments.');
};

const createComment = async (postSlug: string, content: string): Promise<void> => {
  const response = await axios.post(
    `/posts/${postSlug}/comments`,
    { content },
    { headers: await getAuthorizedHeader() }
  );
  if (response.status !== 201) return Promise.reject('Failed to create comment.');
};

const likeComment = async (postSlug: string, commentSlug: string): Promise<void> => {
  const response = await axios.post(`/posts/${postSlug}/comments/${commentSlug}/likes`, null, {
    headers: await getAuthorizedHeader(),
  });
  if (response.status !== 200) return Promise.reject('Failed to like comment.');
};

const unlikeComment = async (postSlug: string, commentSlug: string): Promise<void> => {
  const response = await axios.delete(`/posts/${postSlug}/comments/${commentSlug}/likes`, {
    headers: await getAuthorizedHeader(),
  });
  if (response.status !== 200) return Promise.reject('Failed to unlike comment.');
};

const deleteComment = async (postSlug: string, commentSlug: string): Promise<void> => {
  const response = await axios.delete(`/posts/${postSlug}/comments/${commentSlug}`, {
    headers: await getAuthorizedHeader(),
  });
  if (response.status !== 200) return Promise.reject('Failed to delete comment.');
};

export type { CreatePostRequest };
export {
  createPost,
  retrievePosts,
  deletePost,
  postLike,
  deleteLike,
  getPostCounters,
  getPostLikes,
  createComment,
  deleteComment,
  likeComment,
  unlikeComment,
};
