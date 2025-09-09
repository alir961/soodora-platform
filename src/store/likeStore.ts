import { defineStore } from 'pinia';
import { callPostLike, callPostUnlike, callGetPostLikes } from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';

interface LikeState {
  liked: Record<string, boolean>;
  counts: Record<string, number>;
  loading: Record<string, boolean>;
}

export const useLikeStore = defineStore('likes', {
  state: (): LikeState => ({
    liked: {},
    counts: {},
    loading: {},
  }),
  getters: {
    isLiked: (state) => (slug: string) => state.liked[slug] === true,
    likeCount: (state) => (slug: string) => state.counts[slug] ?? 0,
  },
  actions: {
    initFromPost(post: { slug: string; likeCount?: number }) {
      if (this.counts[post.slug] === undefined && typeof post.likeCount === 'number') {
        this.counts[post.slug] = post.likeCount;
      }
    },
    async ensureLikedLoaded(slug: string) {
      const auth = useAuthStore();
      const userSlug = auth.getAuthenticatedUserSlug;
      if (!userSlug) return;
      if (this.liked[slug] !== undefined || this.loading[slug]) return;
      this.loading[slug] = true;
      try {
        const likes = await callGetPostLikes(slug);
        this.counts[slug] = likes.length;
        this.liked[slug] = likes.includes(userSlug);
      } catch {
        /* I know this catches nothing, I will make it catch in the MR for unauthenticated user errors*/
      } finally {
        delete this.loading[slug];
      }
    },
    async toggle(slug: string) {
      const currently = this.liked[slug] === true;
      this.liked[slug] = !currently;
      if (this.counts[slug] === undefined) this.counts[slug] = 0;
      this.counts[slug] += currently ? -1 : 1;
      if (this.counts[slug] < 0) this.counts[slug] = 0;
      try {
        if (currently) await callPostUnlike(slug);
        else await callPostLike(slug);
      } catch {
        this.liked[slug] = currently;
        this.counts[slug] += currently ? 1 : -1;
        if (this.counts[slug] < 0) this.counts[slug] = 0;
      }
    },
  },
});
