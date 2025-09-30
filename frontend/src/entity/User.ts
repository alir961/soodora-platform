type Role = 'ADMIN' | 'ARTIST' | 'PREMIUM' | 'USER';

interface UserProfile {
  slug: string;
  username: string;
  description: string;
  avatarUrl: string | null;
}

interface User extends UserProfile {
  slug: string;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  description: string;
  avatarUrl: string | null;
  roles: Role[];
}

export type { Role, UserProfile, User };
