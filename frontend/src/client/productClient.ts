import axios from 'axios';
import { Product, ProductAnalytics } from '@/entity/Product';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { ProductSearchParams } from '@/entity/ProductSearch';

interface ProductPage {
  content: Product[];
  hasMore: boolean;
}

async function createProduct(product: FormData): Promise<void> {
  try {
    await axios.post('/products', product, { headers: await getAuthorizedHeader() });
  } catch (error) {
    if (axios.isAxiosError(error)) throw new Error(error.response?.data?.reason);
  }
}

async function callSearchProducts(params: ProductSearchParams): Promise<ProductPage> {
  return axios
    .get('/products/search', { params: { ...params } })
    .then((response) => {
      if (!response || response.status !== 200 || !response.data) return reject('Unable to search products');

      const hasMore = !response.data.empty && response.data.content.length === params.size;
      return {
        content: response.data.content,
        hasMore,
      };
    })
    .catch(reject);
}

async function updateProduct(slug: string, formData: FormData): Promise<Product> {
  return axios
    .put(`/products/${slug}`, formData, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data;
      return reject('Unable to update product');
    })
    .catch(reject);
}

async function callDeleteProduct(slug: string): Promise<void> {
  return axios
    .delete(`/products/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200) return;
      return reject('Unable to delete product');
    })
    .catch(reject);
}

async function callRetrieveProductAnalytics(slug: string): Promise<ProductAnalytics> {
  const response = await axios.get(`/products/${slug}/analytics`, {
    headers: await getAuthorizedHeader(),
  });

  if (response.status === 200 && response.data) return response.data as ProductAnalytics;
  return reject('Failed to retrieve post analytics.');
}

export type { ProductPage };
export { callSearchProducts, createProduct, callDeleteProduct, updateProduct, callRetrieveProductAnalytics };
