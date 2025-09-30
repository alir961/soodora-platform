export interface ProductSearchParams {
  name?: string;
  description?: string;
  category?: string;
  userSlug?: string;
  page: number;
  size: number;
  sort: string;
}
