import instagramIcon from '@/assets/icons/instagram.svg';
import xIcon from '@/assets/icons/x.svg';
import youtubeIcon from '@/assets/icons/youtube.svg';
import soundcloudIcon from '@/assets/icons/soundcloud.svg';
import tiktokIcon from '@/assets/icons/tiktok.svg';
import { linkOutline } from 'ionicons/icons';

interface Link {
  slug: string;
  title: string;
  description: string;
  url: string;
  iconUrl: string;
}

export const platformIcons: Record<string, string> = {
  instagram: instagramIcon,
  x: xIcon,
  youtube: youtubeIcon,
  soundcloud: soundcloudIcon,
  tiktok: tiktokIcon,
  other: linkOutline,
};

export const iconOptions = [
  { label: 'Instagram', value: 'instagram' },
  { label: 'X', value: 'x' },
  { label: 'YouTube', value: 'youtube' },
  { label: 'SoundCloud', value: 'soundcloud' },
  { label: 'TikTok', value: 'tiktok' },
  { label: 'Other', value: 'other' },
];

export const platformUrlPrefixes: Record<string, string> = {
  instagram: 'https://www.instagram.com/',
  x: 'https://x.com/',
  youtube: 'https://www.youtube.com/',
  soundcloud: 'https://soundcloud.com/',
  tiktok: 'https://www.tiktok.com/@',
  other: '',
};

export type { Link };
