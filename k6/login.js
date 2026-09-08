import http from 'k6/http';


export function login(baseUrl, studentId, loginMode = 'ENROLL') {
  const loginRes = http.post(
    `${baseUrl}/api/v1/auth/login`,
    JSON.stringify({
      studentId: studentId,
      loginMode: loginMode,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );



  const token = loginRes.json().data.token;

  if (!token) {
    throw new Error(`JWT가 없습니다 (${studentId}).`);
  }

  return token;
}