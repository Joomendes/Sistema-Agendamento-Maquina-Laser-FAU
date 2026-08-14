function csrfHeaders() {
  const token = document.querySelector('meta[name="_csrf"]')?.content;
  const headerName = document.querySelector('meta[name="_csrf_header"]')?.content;
  return token && headerName ? { [headerName]: token } : {};
}
