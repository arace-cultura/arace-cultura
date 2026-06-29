(function () {
  function renderLucideIcons() {
    if (!window.lucide || typeof window.lucide.createIcons !== 'function') return false;

    window.lucide.createIcons();
    return true;
  }

  function scheduleRender() {
    if (renderLucideIcons()) return;

    let attempts = 0;
    const timer = window.setInterval(() => {
      attempts += 1;
      if (renderLucideIcons() || attempts >= 20) {
        window.clearInterval(timer);
      }
    }, 100);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', scheduleRender);
  } else {
    scheduleRender();
  }

  window.addEventListener('load', renderLucideIcons);
  window.addEventListener('arace:icons', renderLucideIcons);
})();
