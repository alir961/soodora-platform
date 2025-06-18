import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Post, Comment } from '@/entity/Post';
import { PostSearchResponse } from '@/entity/PostSearch';

type CreatePostRequest = Omit<Omit<Post, 'slug'>, 'owner'>;

async function callCreatePost(post: CreatePostRequest): Promise<void> {
  return axios
    .post('/posts', post, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 201) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callRetrievePosts(): Promise<Post[]> {
  try {
    const response = await axios.get('/posts');
    if (response.status === 200 && response.data) {
      return response.data.map((post: Post) => ({
        ...post,
        createdAt: new Date(post.createdAt),
      }));
    }
    return reject('Unable to call API');
  } catch (error) {
    let message: string = 'Post retrieving Failed!';

    if (error instanceof Error) message = error.message;
    return reject(message);
  }
}

async function callDeletePost(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callPostLike(slug: string): Promise<void> {
  return axios
    .post(`/posts/${slug}/likes`, null, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to like post.');
    })
    .catch(reject);
}

async function callPostUnlike(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}/likes`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to remove like.');
    })
    .catch(reject);
}

async function callPostRepost(slug: string): Promise<void> {
  return axios
    .post(`/posts/${slug}/reposts`, null, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to repost post.');
    })
    .catch(reject);
}

async function callPostUnrepost(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}/reposts`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Failed to remove repost.');
    })
    .catch(reject);
}

async function callGetPostLikes(slug: string): Promise<string[]> {
  const response = await axios.get(`/posts/${slug}/likes`);

  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve post likes.');
}

async function callGetPostReposts(slug: string): Promise<string[]> {
  const response = await axios.get(`/posts/${slug}/reposts`);

  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve post likes.');
}

export async function callGetComments(postSlug: string): Promise<Comment[]> {
  const response = await axios.get(`/posts/${postSlug}/comments`);

  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve comments.');
}

async function callCreateComment(postSlug: string, content: string): Promise<void> {
  const response = await axios.post(
    `/posts/${postSlug}/comments`,
    { content },
    { headers: await getAuthorizedHeader() }
  );

  if (response.status !== 201) return reject('Failed to create comment.');
}

async function callLikeComment(postSlug: string, commentSlug: string): Promise<void> {
  const response = await axios.post(`/posts/${postSlug}/comments/${commentSlug}/likes`, null, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status !== 200) return reject('Failed to like comment.');
}

async function callGetCommentLikes(postSlug: string, commentSlug: string): Promise<string[]> {
  const response = await axios.get(`/posts/${postSlug}/comments/${commentSlug}/likes`, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status === 200 && response.data) return response.data;
  return reject('Failed to retrieve comment likes.');
}

async function callUnlikeComment(postSlug: string, commentSlug: string): Promise<void> {
  const response = await axios.delete(`/posts/${postSlug}/comments/${commentSlug}/likes`, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status !== 200) return reject('Failed to unlike comment.');
}

async function callDeleteComment(postSlug: string, commentSlug: string): Promise<void> {
  const response = await axios.delete(`/posts/${postSlug}/comments/${commentSlug}`, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status !== 200) return reject('Failed to delete comment.');
}

async function searchPosts(
  title: string | null,
  description: string | null,
  pageNumber: number,
  pageSize: number,
  sortValue: string
): Promise<PostSearchResponse> {
  try {
    const params = new URLSearchParams();

    if (title) params.append('title', title);
    if (description) params.append('description', description);

    params.append('page', pageNumber.toString());
    params.append('size', pageSize.toString());
    params.append('sort', sortValue);

    const response = await axios.get<PostSearchResponse>(`/posts?${params.toString()}`, {
      headers: await getAuthorizedHeader(),
    });

    return response.data;
  } catch (error) {
    let message: string = 'Posts searching Failed!';

    if (error instanceof Error) message = error.message;
    return reject(message);
  }
}

export type { CreatePostRequest };
export {
  callCreatePost,
  callRetrievePosts,
  callDeletePost,
  callPostLike,
  callPostUnlike,
  callPostRepost,
  callPostUnrepost,
  callGetPostLikes,
  callGetPostReposts,
  callCreateComment,
  callGetCommentLikes,
  callDeleteComment,
  callLikeComment,
  callUnlikeComment,
  searchPosts,
};
