import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Post } from '@/entity/Post';
import { PostSearchResponse } from '@/entity/PostSearch';

interface PostPage {
  content: Post[];
  hasMore: boolean;
}

async function callDeletePost(slug: string): Promise<void> {
  return axios
    .delete(`/posts/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callDeleteComment(postSlug: string, commentSlug: string): Promise<void> {
  const response = await axios.delete(`/posts/${postSlug}/comments/${commentSlug}`, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status !== 200) return reject('Failed to delete comment.');
}

async function callSearchPosts(
  title: string | null,
  description: string | null,
  hasAudio: boolean = false,
  pageNumber: number,
  pageSize: number,
  sortValue: string
): Promise<PostSearchResponse> {
  try {
    const params = new URLSearchParams();

    if (title) params.append('title', title);
    if (description) params.append('description', description);
    if (hasAudio) params.append('hasAudio', hasAudio.toString());

    params.append('page', pageNumber.toString());
    params.append('size', pageSize.toString());
    params.append('sort', sortValue);

    const response = await axios.get<PostSearchResponse>('/posts/search', {
      headers: await getAuthorizedHeader(),
      params: params,
    });

    return response.data;
  } catch (error) {
    let message: string = 'Fetch posts from API failed!';

    if (error instanceof Error) message = error.message;
    return reject(message);
  }
}

export type { PostPage };
export { callDeletePost, callDeleteComment, callSearchPosts };
