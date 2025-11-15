export interface SoodoraKitDTO {
  slug: string;
  title: string;
  shortBio: string | null;
  longBio: string | null;
  contactEmail: string | null;
  contactPhone: string | null;
  items?: SoodoraKitItemDTO[];
}

export interface SoodoraKitItemDTO {
  slug: string;
  type: 'image' | 'video' | 'audio' | 'text' | string;
  title: string;
  content: string;
}

export interface CreateSoodoraKitRequest {
  title: string;
  shortBio?: string | null;
  longBio?: string | null;
  contactEmail?: string | null;
  contactPhone?: string | null;
}

export interface UpdateSoodoraKitRequest extends CreateSoodoraKitRequest {}
