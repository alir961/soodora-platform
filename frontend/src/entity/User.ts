type Role = 'ADMIN' | 'ARTIST' | 'PREMIUM';

interface User {
  slug: string;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  roles: Role[];
}

export type { Role, User };
