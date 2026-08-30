import { login } from './login.js';
import { enterLectureQueue } from './enroll.js';

const BASE_HTTP = 'http://localhost:8081';
const BASE_WS = 'ws://localhost:8081/ws';
const LECTURE_ID = 1;

const VU_COUNT = 1;

export const options = {
  vus: VU_COUNT,
  iterations: VU_COUNT,
};

export default function () {
  const studentNum = __VU;
  const studentId = `TEST${String(studentNum).padStart(3, '0')}`;

  const token = login(BASE_HTTP, studentId);
  enterLectureQueue(BASE_WS, token, LECTURE_ID, studentId);
}