import { User } from '@/entity/User';
import { Link } from '@/entity/Link';

interface LinkPage {
  slug: string;
  title: string;
  description: string;
  owner: User;
  links: Link[];
}

export type { LinkPage };
