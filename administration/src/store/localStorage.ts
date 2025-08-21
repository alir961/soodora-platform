import Cookies from 'js-cookie';

const BEARER_TOKEN_KEY: string = 'BearerToken';

function saveBearerToken(token: string): void {
  Cookies.set(BEARER_TOKEN_KEY, token);
}

function loadBearerToken(): string | null {
  const value = Cookies.get(BEARER_TOKEN_KEY);
  if (!value) return null;
  return decodeURIComponent(value);
}

function deleteBearerToken(): void {
  Cookies.remove(BEARER_TOKEN_KEY);
}

export { saveBearerToken, deleteBearerToken, loadBearerToken };
