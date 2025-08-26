import { UserProfile } from '@/entity/User';

interface Artist extends UserProfile {
  slug: string;
  username: string;
  description: string;
  avatarUrl: string | null;
}

export type { Artist };
