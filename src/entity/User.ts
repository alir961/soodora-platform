type Role = 'ADMIN' | 'ARTIST' | 'PREMIUM' | 'USER';

interface User {
  slug: string;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  description: string;
  avatarUrl: string | null;
  roles: Role[];
}

export type { Role, User };
