import axios from 'axios';

export interface CheckoutSessionRequest {
  name: string;
  amount: number;
  currency: string;
  imageUrl?: string | null;
  productSlug: string;
  vendorSlug: string;
  buyerSlug: string;
  quantity: number;
}

export interface CheckoutSessionResponse {
  url: string;
}

export async function createCheckoutSession(request: CheckoutSessionRequest): Promise<CheckoutSessionResponse> {
  return axios
    .post('/payments/create-checkout-session', request)
    .then((response) => {
      if (!response || response.status !== 200) {
        return Promise.reject('Unable to create checkout session');
      }
      return response.data as CheckoutSessionResponse;
    })
    .catch((err) => Promise.reject(err));
}
