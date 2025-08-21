import axios from 'axios';
import { Product, ProductSearchParams } from '@/entity/Product';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';

interface ProductPage {
  content: Product[];
  hasMore: boolean;
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

async function callDeleteProduct(slug: string): Promise<void> {
  return axios
    .delete(`/products/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

export type { ProductPage };
export { callSearchProducts, callDeleteProduct };
