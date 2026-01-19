import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import {
  QuoteDTO,
  QuoteArticleDTO,
  CreateQuoteRequest,
  UpdateQuoteRequest,
  CreateQuoteArticleRequest,
  UpdateQuoteArticleRequest,
  QuoteSearchParams,
  QuotePage,
} from '@/entity/Quote';
import { reject } from '@/utils';

const BASE = '/quotes';

export async function createQuote(request: CreateQuoteRequest): Promise<QuoteDTO> {
  return axios
    .post(BASE, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data?.quoteDTO) return response.data.quoteDTO;
      return reject('Failed to create Quote');
    })
    .catch(reject);
}

export async function updateQuote(slug: string, request: UpdateQuoteRequest): Promise<QuoteDTO> {
  return axios
    .put(`${BASE}/${slug}`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.quoteDTO) return response.data.quoteDTO;
      return reject('Failed to update Quote');
    })
    .catch(reject);
}

export async function getQuote(slug: string): Promise<QuoteDTO> {
  return axios
    .get(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.quoteDTO) return response.data.quoteDTO;
      return reject('Quote not found');
    })
    .catch(reject);
}

export async function searchQuotes(params: QuoteSearchParams): Promise<QuotePage> {
  return axios
    .get(`${BASE}/search`, { headers: await getAuthorizedHeader(), params })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data;
      return reject('No quotes found');
    })
    .catch(reject);
}

export async function deleteQuote(slug: string): Promise<void> {
  return axios
    .delete(`${BASE}/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Failed to delete Quote');
    })
    .catch(reject);
}

export async function createQuoteArticle(request: CreateQuoteArticleRequest): Promise<QuoteArticleDTO> {
  return axios
    .post(`${BASE}/articles`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 201 && response.data?.quoteArticleDTO) return response.data.quoteArticleDTO;
      return reject('Failed to create Quote Article');
    })
    .catch(reject);
}

export async function updateQuoteArticle(slug: string, request: UpdateQuoteArticleRequest): Promise<QuoteArticleDTO> {
  return axios
    .put(`${BASE}/articles/${slug}`, request, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.quoteArticleDTO) return response.data.quoteArticleDTO;
      return reject('Failed to update Quote Article');
    })
    .catch(reject);
}

export async function getQuoteArticle(slug: string): Promise<QuoteArticleDTO> {
  return axios
    .get(`${BASE}/articles/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data?.quoteArticleDTO) return response.data.quoteArticleDTO;
      return reject('Quote Article not found');
    })
    .catch(reject);
}

export async function getAllQuoteArticles(pageable?: { page: number; size: number }): Promise<QuoteArticleDTO[]> {
  return axios
    .get(`${BASE}/articles`, { headers: await getAuthorizedHeader(), params: pageable })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data;
      return reject('No Quote Articles found');
    })
    .catch(reject);
}

export async function deleteQuoteArticle(slug: string): Promise<void> {
  return axios
    .delete(`${BASE}/articles/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return;
      return reject('Failed to delete Quote Article');
    })
    .catch(reject);
}

export async function exportQuotePdf(slug: string): Promise<Blob> {
  return axios
    .get(`${BASE}/${slug}/export/pdf`, { headers: await getAuthorizedHeader(), responseType: 'blob' })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as Blob;
      return reject('Failed to export Quote PDF');
    })
    .catch(reject);
}
