type EventType =
  | 'CONCERT'
  | 'FESTIVAL'
  | 'COMPETITION'
  | 'PERFORMANCE_SHOW'
  | 'EDUCATIONAL'
  | 'DANCE_MUSIC'
  | 'RELIGIOUS_SPIRITUAL'
  | 'OTHER';

interface Event {
  slug: string;
  type: EventType;
  title: string;
  description: string;
  url: string;
  thumbnailUrl: string;
  latitude: number;
  longitude: number;
  dateTime: string;
}

export type { EventType, Event };
