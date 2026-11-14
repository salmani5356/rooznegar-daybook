'use strict';

const CACHE_NAME = 'rooznegar-shell-v5';
const APP_SHELL = [
  './',
  './index.html',
  './manifest.webmanifest',
  './icon.svg',
  './icon-192.png',
  './icon-512.png',
  './assets/fonts/Vazirmatn-var.woff2',
  './assets/vendor/supabase.js',
  './assets/journal-vignette.svg',
  './assets/daily-1.svg',
  './assets/daily-2.svg',
  './assets/daily-3.svg',
  './assets/daily-4.svg',
  './assets/daily-5.svg',
  './assets/wc-1.svg',
  './assets/wc-2.svg',
  './assets/wc-3.svg',
  './assets/wc-4.svg',
  './assets/wc-5.svg',
  './privacy.html'
];

// مسیر ریشه‌ی برنامه (مثلاً /rooznegar-daybook/)؛ فقط صفحه‌ی اصلی باید به‌عنوان پوسته‌ی آفلاین ذخیره شود.
const ROOT_PATH = new URL('./', self.location).pathname;
const isAppPage = url => url.pathname === ROOT_PATH || url.pathname === ROOT_PATH + 'index.html';

self.addEventListener('install', event => {
  event.waitUntil((async () => {
    const cache = await caches.open(CACHE_NAME);
    // Cache each local file independently: a missing optional file
    // must not prevent the service worker from installing.
    await Promise.all(APP_SHELL.map(async path => {
      try {
        const response = await fetch(path, { cache: 'reload' });
        if (response.ok) await cache.put(path, response);
      } catch (_) {}
    }));
    await self.skipWaiting();
  })());
});

self.addEventListener('activate', event => {
  event.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter(key => key.startsWith('rooznegar-') && key !== CACHE_NAME)
      .map(key => caches.delete(key)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', event => {
  const request = event.request;
  if (request.method !== 'GET') return;

  const url = new URL(request.url);
  if (url.origin !== self.location.origin) return;

  // Navigation: network first, then cached copy for offline use.
  if (request.mode === 'navigate') {
    event.respondWith((async () => {
      try {
        const response = await fetch(request);
        // فقط خود برنامه در کش پوسته ذخیره می‌شود (نه privacy.html یا صفحه‌های دیگر).
        if (response.ok && isAppPage(url)) {
          const cache = await caches.open(CACHE_NAME);
          cache.put('./index.html', response.clone()).catch(() => {});
        }
        return response;
      } catch (_) {
        return (await caches.match(request, { ignoreSearch: true })) ||
          (await caches.match('./index.html')) ||
          (await caches.match('./')) ||
          new Response('برای استفاده آفلاین، ابتدا روزنگار را یک بار با اینترنت باز کن.', {
            status: 503, headers: { 'Content-Type': 'text/plain; charset=utf-8' }
          });
      }
    })());
    return;
  }

  // Same-origin static files: cache first, then network and update cache.
  event.respondWith((async () => {
    const cached = await caches.match(request);
    if (cached) return cached;
    try {
      const response = await fetch(request);
      if (response.ok && response.status === 200) {
        const cache = await caches.open(CACHE_NAME);
        cache.put(request, response.clone()).catch(() => {});
      }
      return response;
    } catch (_) {
      return new Response('', { status: 504, statusText: 'Offline' });
    }
  })());
});
