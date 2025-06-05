type Role = 'ADMIN' | 'ARTIST' | 'PREMIUM';

interface User {
  slug: string;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  roles: Role[];
  avatarUrl: string | null;
}

export type { Role, User };
