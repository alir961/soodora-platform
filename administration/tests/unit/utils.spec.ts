import { describe, expect, test } from 'vitest';
import { capitalize, reject } from '@/utils';

describe('capitalize', () => {
  test('upper-cases the first letter and lower-cases the rest', () => {
    expect(capitalize('aRTIST')).toBe('Artist');
  });

  test('leaves an empty string unchanged', () => {
    expect(capitalize('')).toBe('');
  });
});

describe('reject', () => {
  test('wraps a message in an Error', async () => {
    await expect(reject('Forbidden')).rejects.toThrow('Forbidden');
  });

  test('keeps an existing Error as is', async () => {
    const error = new Error('Network');
    await expect(reject(error)).rejects.toBe(error);
  });
});
