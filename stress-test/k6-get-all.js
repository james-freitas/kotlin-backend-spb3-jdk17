import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 }, // Ramp up to 10 users over 10 seconds
    ],
};

export default function() {
    const url = 'http://localhost:8080/api/v1/categories';   

    const res = http.get(url);

    // Optionally check for errors
    if (res.status !== 200) {
        console.error(`Request failed. Status: ${res.status}, Body: ${res.body}`);
    }

    sleep(1); 
}
