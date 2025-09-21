import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { Sale } from '@/entity/Sale';

export async function getMySales(page = 0, size = 10, sort = 'createdAt'): Promise<Sale[]> {
  const headers = await getAuthorizedHeader();
  const response = await axios.get(`/sales/:me`, {
    headers,
    params: { page, size, sort },
  });

  if (response.status === 200 && response.data) {
    return response.data.content as Sale[];
  }
  return Promise.reject('No sales found');
}
