export interface MediaKitDTO {
  slug: string;
  title: string;
  shortBio: string | null;
  longBio: string | null;
  contactEmail: string | null;
  contactPhone: string | null;
  items?: MediaKitItemDTO[];
}

export interface MediaKitItemDTO {
  slug: string;
  type: 'image' | 'video' | 'audio' | 'text' | string;
  title: string;
  content: string;
}

export interface CreateMediaKitRequest {
  title: string;
  shortBio?: string | null;
  longBio?: string | null;
  contactEmail?: string | null;
  contactPhone?: string | null;
}

export interface UpdateMediaKitRequest extends CreateMediaKitRequest {}

export interface CreateMediaKitItemRequest {
  type: string;
  title: string;
  content: string;
}

export interface UpdateMediaKitItemRequest extends CreateMediaKitItemRequest {}
