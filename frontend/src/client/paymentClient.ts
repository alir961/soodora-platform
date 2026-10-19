import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';

// The server reads price, vendor and buyer itself: the client only says what to buy
export interface CheckoutSessionRequest {
  productSlug: string;
  quantity: number;
}

export interface CheckoutSessionResponse {
  url: string;
}

export async function createCheckoutSession(request: CheckoutSessionRequest): Promise<CheckoutSessionResponse> {
  return axios
    .post('/payments/create-checkout-session', request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) {
        return Promise.reject('Unable to create checkout session');
      }
      return response.data as CheckoutSessionResponse;
    })
    .catch((err) => Promise.reject(err));
}
