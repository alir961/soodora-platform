function reject(reason: string | Error) {
  if (reason instanceof Error) return Promise.reject(reason);
  return Promise.reject(new Error(reason));
}

function capitalize(value: string): string {
  if (value.length < 1) return value;
  return value[0].toUpperCase() + value.slice(1).toLowerCase();
}

export { reject, capitalize };
