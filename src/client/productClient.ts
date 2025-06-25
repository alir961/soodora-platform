import axios from 'axios';
import { Product } from '@/entity/Product';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';

type CreateProductRequest = Omit<Omit<Product, 'slug'>, 'vendor'>;

interface ProductPage {
  content: Product[];
  hasMore: boolean;
}

async function createProduct(product: CreateProductRequest): Promise<void> {
  return axios.post('/products', product, { headers: await getAuthorizedHeader() });
}

async function retrieveProducts(): Promise<Product[]> {
  return axios.get('/products').then((response) => {
    if (response && response.status === 200 && response.data) return response.data as Product[];
    return reject('Unable to call API');
  });
}

export type { CreateProductRequest, ProductPage };
export { retrieveProducts, createProduct };
