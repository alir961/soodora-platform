import { User } from '@/entity/User';
import { Link } from '@/entity/Link';

interface LinkHub {
  slug: string;
  title: string;
  description: string;
  owner: User;
  links: Link[];
}

interface LinkHubAnalytics {
  viewCount: number;
}

export type { LinkHub, LinkHubAnalytics };
