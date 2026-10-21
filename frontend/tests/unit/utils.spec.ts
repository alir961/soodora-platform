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

describe('escapeHtml', () => {
  test('turns markup into text', async () => {
    const { escapeHtml } = await import('@/utils');
    expect(escapeHtml(`<img src=x onerror="alert('x')">&`)).toBe(
      '&lt;img src=x onerror=&quot;alert(&#39;x&#39;)&quot;&gt;&amp;'
    );
  });
});

describe('safeUrl', () => {
  test('keeps http(s) links and drops other schemes', async () => {
    const { safeUrl } = await import('@/utils');
    expect(safeUrl('https://soodora.com/event?id=1')).toBe('https://soodora.com/event?id=1');
    expect(safeUrl('javascript:alert(1)')).toBeNull();
    expect(safeUrl('data:text/html,<script>alert(1)</script>')).toBeNull();
    expect(safeUrl('not a url')).toBeNull();
    expect(safeUrl(undefined)).toBeNull();
  });

  test('encodes characters that could leave a CSS url()', async () => {
    const { safeUrl } = await import('@/utils');
    expect(safeUrl("https://x.io/a.png')}body{color:red")).toBe('https://x.io/a.png%27%29%7Dbody%7Bcolor:red');
  });
});
