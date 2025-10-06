export interface Money {
  amount: number;
  currency: Currency;
  symbol: string;
}

export type Currency = 'EUR' | 'USD' | 'CAD' | 'GBP';
