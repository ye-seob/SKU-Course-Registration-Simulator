import { login } from './login.js';
import { enterLectureQueue } from './enroll.js';
import http from 'k6/http';
const BASE_HTTP = 'http://localhost:8081';
const BASE_WS = 'ws://localhost:8081/ws';
const LECTURE_ID = 1;

const VU_COUNT = 1;

export const options = {
  scenarios: {
    default: {
      executor: 'per-vu-iterations',
      vus: VU_COUNT,
      iterations: 1,
      maxDuration: '10m',
    },
  },
};

export default function () {
  const studentNum = __VU;
  const studentId = `TEST${String(studentNum).padStart(3, '0')}`;

  const token = login(BASE_HTTP, studentId);
  enterLectureQueue(BASE_WS, token, LECTURE_ID, studentId);
}
export function teardown() {
  const resetUrl = 'http://localhost:8081/test/reset';
  http.del(resetUrl);
}