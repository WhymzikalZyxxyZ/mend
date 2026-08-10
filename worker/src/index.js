'use strict';

// ── CORS ──────────────────────────────────────────────────────────────────────
// Only the app's own origin and the portfolio site are allowed to call this API.
const ALLOWED_ORIGIN_RE = /^https:\/\/([a-z0-9-]+\.)?zyxwonderland\.xyz$/;

function corsHeaders(origin) {
    if (!ALLOWED_ORIGIN_RE.test(origin)) return {};
    return {
        'Access-Control-Allow-Origin':  origin,
        'Access-Control-Allow-Methods': 'GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type',
        'Access-Control-Max-Age':       '86400',
        'Vary':                         'Origin',
    };
}

const SEC_HEADERS = {
    'X-Content-Type-Options': 'nosniff',
    'X-Frame-Options':        'DENY',
    'Referrer-Policy':        'no-referrer',
};

function json(data, status = 200, extraHeaders = {}) {
    return new Response(JSON.stringify(data), {
        status,
        headers: { 'Content-Type': 'application/json', ...SEC_HEADERS, ...extraHeaders },
    });
}

export default {
    async fetch(request, env) {
        const url = new URL(request.url);
        const origin = request.headers.get('Origin') || '';
        const cors = corsHeaders(origin);

        if (request.method === 'OPTIONS') {
            return new Response(null, { status: 204, headers: cors });
        }

        if (url.pathname === '/health') {
            return json({ status: 'ok', service: 'mend', env: env.SITE_ORIGIN ?? null }, 200, cors);
        }

        return json({ error: 'not found' }, 404, cors);
    },
};
