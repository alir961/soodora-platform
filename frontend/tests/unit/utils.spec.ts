import { describe, expect, test, vi } from 'vitest';

vi.mock('@ionic/vue', () => ({ toastController: { create: vi.fn() } }));
vi.mock('@/axiosConfiguration', () => ({ retrieveCallbackURL: () => '' }));

const { capitalize, formatDate, reject } = await import('@/utils');

describe('formatDate', () => {
  test('formats as dd/mm/yyyy hh:mm with zero padding', () => {
    const iso = new Date(2026, 0, 5, 9, 7).toISOString();
    expect(formatDate(iso)).toBe('05/01/2026 09:07');
  });
});

describe('capitalize', () => {
  test('upper-cases the first letter and lower-cases the rest', () => {
    expect(capitalize('mUSIC')).toBe('Music');
    expect(capitalize('')).toBe('');
  });
});

describe('reject', () => {
  test('always rejects with an Error', async () => {
    await expect(reject('Unauthorized')).rejects.toBeInstanceOf(Error);
  });
});
