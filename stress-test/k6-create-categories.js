import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5s', target: 10 },     // Ramp up
        { duration: '10s', target: 10 },    // Stay at 10 users
        { duration: '5s', target: 0 },      // Ramp down
    ],
    thresholds: {
        'http_req_duration': ['p(95)<200'], // 95% of requests should be below 1000ms
        'http_req_failed': ['rate<0.01'],   // Error rate should be less than 1%
    },
}

export default function () {
    const url = 'http://localhost:8080/api/v1/categories';
    const payload = JSON.stringify({ name: `cat_${__VU}_${__ITER}` });

    const params = {
        headers: {
          'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params, { tags: { endpoint: 'create-categories', method: 'POST' } });

    check(res, {
      'is status 201': (r) => r.status === 201,
      'response contains expected text': (r) => r.body.includes('id'),
    });

    sleep(1);

    tearDown();

    sleep(1);
}

export function tearDown() {
    const url = 'http://localhost:8080/api/v1/categories/cleanup';

    const res = http.post(url, null, null);
    if (res.status !== 200) {
        console.error(`Cleanup failed: ${res.status} - ${res.body}`);
    }
}