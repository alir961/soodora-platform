import axios from 'axios';
import { reject } from '@/utils';
import { ProductAnalytics } from '@/entity/Product';
import { PostAnalytics } from '@/entity/Post';
import { getAuthorizedHeader } from '@/client/authenticationClient';

async function callComputePostsAnalytics(): Promise<PostAnalytics> {
  return axios
    .get('/posts/analytics', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data as PostAnalytics;
      return reject('Unable to compute posts analytics.');
    })
    .catch(reject);
}

async function callComputeProductsAnalytics(): Promise<ProductAnalytics> {
  return axios
    .get('/products/analytics', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response.status === 200 && response.data) return response.data as ProductAnalytics;
      return reject('Unable to compute products analytics.');
    })
    .catch(reject);
}

export { callComputePostsAnalytics, callComputeProductsAnalytics };
