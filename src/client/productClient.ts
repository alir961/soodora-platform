import axios from 'axios';
import { Product, ProductSearchParams } from '@/entity/Product';
import { getAuthorizedHeader } from '@/client/authenticationClient';

type CreateProductRequest = Omit<Omit<Product, 'slug'>, 'vendor'>;

interface ProductPage {
  content: Product[];
  hasMore: boolean;
}

async function createProduct(product: CreateProductRequest): Promise<void> {
  return axios.post('/products', product, { headers: await getAuthorizedHeader() });
}

async function callSearchProducts(params: ProductSearchParams): Promise<ProductPage> {
  const response = await axios.get('/products/search', {
    params: {
      ...params,
    },
  });

  const hasMore = !response.data.empty && response.data.content.length === params.size;
  return {
    content: response.data.content,
    hasMore,
  };
}

export type { CreateProductRequest, ProductPage };
export { callSearchProducts, createProduct };
