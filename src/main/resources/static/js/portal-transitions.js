(() => {
  const portalRoutes = new Set(['/app', '/agendar', '/cancelar', '/suplente']);

  document.addEventListener('click', (event) => {
    const link = event.target.closest('a[href]');
    if (!link || event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;

    const destination = new URL(link.href, window.location.href);
    if (destination.origin !== window.location.origin || !portalRoutes.has(destination.pathname)) return;
    if (destination.pathname === window.location.pathname) return;

    event.preventDefault();
    document.querySelector('.shell')?.classList.add('portal-leaving');
    window.setTimeout(() => { window.location.href = destination.href; }, 180);
  });
})();
