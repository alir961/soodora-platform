<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { IonIcon } from '@ionic/vue';
import { arrowUp, heart, heartOutline } from 'ionicons/icons';
import trashIcon from '@/assets/icons/trash.svg';
import { Comment } from '@/entity/Post';
import {
  callCreateComment,
  callGetComments,
  callDeleteComment,
  callGetCommentLikes,
  callLikeComment,
  callUnlikeComment,
} from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { showToast } from '@/utils';
import UsernameTag from '@/component/user/UsernameTag.vue';
import Avatar from '@/component/user/AvatarItem.vue';
import DateDisplay from '@/component/date/DateDisplay.vue';

const props = defineProps({
  postSlug: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['close', 'comment-created']);

const authStore = useAuthStore();
const showSheet = ref(false);
const comments = ref<Comment[]>([]);
const commentInput = ref('');
const commentLikes = ref<Record<string, boolean>>({});
const isLoading = ref(false);

const maxHeight = computed(() => {
  const commentCount = comments.value.length;
  if (commentCount === 0) return '40vh';
  if (commentCount <= 2) return '50vh';
  if (commentCount <= 5) return '65vh';
  return '80vh';
});

const loadComments = async () => {
  try {
    isLoading.value = true;
    comments.value = await callGetComments(props.postSlug);

    // Load likes for all comments
    const userSlug = authStore.getAuthenticatedUserSlug;
    if (userSlug) {
      for (const comment of comments.value) {
        try {
          const likes = await callGetCommentLikes(props.postSlug, comment.slug);
          commentLikes.value[comment.slug] = likes.some((like) => like === userSlug);
        } catch (error) {
          console.error('Unable to load likes for comment', error);
        }
      }
    }
  } catch (error) {
    console.error('Failed to load comments:', error);
  } finally {
    isLoading.value = false;
  }
};

const createComment = async () => {
  const content = commentInput.value.trim();

  if (!content) {
    await showToast("Comment can't be empty.", 'warning');
    return;
  }

  try {
    await callCreateComment(props.postSlug, content);
    commentInput.value = '';
    await loadComments();
    emit('comment-created');
  } catch (error: unknown) {
    const message = 'Failed to drop your comment. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const toggleLike = async (commentSlug: string) => {
  try {
    const isLiked = commentLikes.value[commentSlug];

    if (isLiked) {
      await callUnlikeComment(props.postSlug, commentSlug);
      commentLikes.value[commentSlug] = false;
    } else {
      await callLikeComment(props.postSlug, commentSlug);
      commentLikes.value[commentSlug] = true;
    }
  } catch (error) {
    console.error('Failed to toggle like:', error);
  }
};

const handleDelete = async (commentSlug: string) => {
  try {
    await callDeleteComment(props.postSlug, commentSlug);
    await loadComments();
  } catch (error) {
    console.error('Failed to delete comment:', error);
  }
};

const open = () => {
  showSheet.value = true;
  loadComments();
};

const close = () => {
  showSheet.value = false;
  emit('close');
};

defineExpose({ open, close });

watch(
  () => props.postSlug,
  () => {
    if (showSheet.value) {
      loadComments();
    }
  }
);

onMounted(() => {
  if (showSheet.value) {
    loadComments();
  }
});
</script>

<template>
  <Teleport to="body">
    <transition name="sheet-slide">
      <div v-if="showSheet" class="comments-overlay" @click="close">
        <div class="comments-sheet" :style="{ maxHeight }" @click.stop>
          <div class="sheet-handle"></div>

          <div class="sheet-header">
            <h2>Comments</h2>
            <span class="comment-count"
              >{{ comments.length }} {{ comments.length === 1 ? 'comment' : 'comments' }}</span
            >
          </div>

          <div class="comments-list">
            <div v-if="isLoading" class="loading-state">
              <p>Loading comments...</p>
            </div>

            <div v-else-if="comments.length === 0" class="empty-state">
              <p>No comments yet. Be the first to share your thoughts!</p>
            </div>

            <div v-else v-for="comment in comments" :key="comment.slug" class="comment-item">
              <div class="comment-avatar">
                <Avatar :user="comment.user" />
              </div>
              <div class="comment-body">
                <div class="comment-header">
                  <UsernameTag :user="comment.user" />
                </div>
                <div class="comment-content">
                  <p>{{ comment.content }}</p>
                </div>
              </div>
              <div class="comment-right">
                <DateDisplay :date="comment.createdAt" class="comment-date" />
                <div class="comment-actions">
                  <button
                    class="like-btn"
                    :class="{ active: commentLikes[comment.slug] }"
                    @click="toggleLike(comment.slug)">
                    <ion-icon :icon="commentLikes[comment.slug] ? heart : heartOutline" />
                  </button>
                  <button
                    v-if="comment.user.slug === authStore.getAuthenticatedUserSlug"
                    class="delete-btn"
                    @click="handleDelete(comment.slug)">
                    <img :src="trashIcon" alt="Delete" class="delete-icon" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="comment-input-container">
            <div class="input-wrapper">
              <input
                v-model="commentInput"
                type="text"
                placeholder="Drop a comment..."
                @keyup.enter="createComment"
                class="comment-input" />
              <button class="send-btn" @click="createComment" :disabled="!commentInput.trim()">
                <ion-icon :icon="arrowUp" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.comments-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
}

.comments-sheet {
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.98), rgba(18, 22, 30, 0.95));
  border-radius: 24px 24px 0 0;
  padding: 0;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: none;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.4);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sheet-handle {
  width: 40px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  margin: 1rem auto 0.5rem auto;
  flex-shrink: 0;
}

.sheet-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}

.sheet-header h2 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.25rem 0;
}

.comment-count {
  font-size: 0.9rem;
  color: #9aa3ad;
  font-weight: 500;
}

.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 1.5rem;
  min-height: 100px;
  max-height: calc(100% - 180px);
}

.loading-state,
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  text-align: center;
}

.loading-state p,
.empty-state p {
  color: #9aa3ad;
  font-size: 0.95rem;
  margin: 0;
}

.comment-item {
  padding: 1.25rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  gap: 0.75rem;
  align-items: flex-start;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
  align-self: flex-start;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comment-header .username {
  font-size: 0.95rem;
  font-weight: 600;
}

.comment-content {
  display: flex;
  flex-direction: column;
}

.comment-content p {
  font-size: 0.95rem;
  color: #cfcfcf;
  line-height: 1.4;
  margin: 0;
  word-break: break-word;
}

.comment-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  flex-shrink: 0;
  align-self: stretch;
}

.comment-date {
  font-size: 0.8rem;
  color: #9aa3ad;
  white-space: nowrap;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.like-btn,
.delete-btn {
  appearance: none;
  border: none;
  background: transparent;
  color: #9aa3ad;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  border-radius: 4px;
}

.like-btn {
  font-size: 1.1rem;
}

.like-btn:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.like-btn.active {
  color: #ef4444;
}

.delete-btn {
  padding: 0.35rem;
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
}

.delete-icon {
  width: 16px;
  height: 16px;
  filter: brightness(0) saturate(100%) invert(62%) sepia(8%) saturate(366%) hue-rotate(169deg) brightness(91%)
    contrast(87%);
}

.delete-btn:hover .delete-icon {
  filter: brightness(0) saturate(100%) invert(42%) sepia(93%) saturate(4370%) hue-rotate(346deg) brightness(96%)
    contrast(94%);
}

.comment-input-container {
  margin-bottom: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(18, 22, 30, 0.95);
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 0.5rem 0.5rem 0.5rem 1.25rem;
  transition: all 0.2s ease;
}

.input-wrapper:focus-within {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(255, 255, 255, 0.06);
}

.comment-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #ffffff;
  font-size: 0.95rem;
  padding: 0;
}

.comment-input::placeholder {
  color: #9aa3ad;
}

.send-btn {
  appearance: none;
  border: none;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: #ffffff;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.send-btn ion-icon {
  font-size: 1.1rem;
}

.send-btn:not(:disabled):hover {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  transform: scale(1.05);
}

.send-btn:not(:disabled):active {
  transform: scale(0.95);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.sheet-slide-enter-active {
  transition: opacity 0.3s ease;
}

.sheet-slide-leave-active {
  transition: opacity 0.25s ease;
}

.sheet-slide-enter-from,
.sheet-slide-leave-to {
  opacity: 0;
}

.sheet-slide-enter-active .comments-sheet {
  animation: slideUp 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.sheet-slide-leave-active .comments-sheet {
  animation: slideDown 0.3s cubic-bezier(0.8, 0.2, 0.8, 0.2);
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(100%);
  }
}

@media (max-width: 768px) {
  .sheet-header {
    padding: 0.75rem 1.25rem;
  }

  .sheet-header h2 {
    font-size: 1.2rem;
  }

  .comments-list {
    padding: 0 1.25rem;
  }

  .comment-input-container {
    padding: 0.75rem 1.25rem;
  }

  .comment-content p {
    font-size: 0.9rem;
  }
}
</style>
