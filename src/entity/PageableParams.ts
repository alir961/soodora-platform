interface SortPageable {
  page: number;
  size: number;
  sort: string;
}

interface Pageable {
  page: number;
  size: number;
}

export type { SortPageable, Pageable };
