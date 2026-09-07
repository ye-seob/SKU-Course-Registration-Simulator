import ws from 'k6/ws';
import { Trend, Counter } from 'k6/metrics';

const wsConnectTime = new Trend('ws_connect_time');
const wsEnterLatency = new Trend('ws_enter_latency');
const registrationLatency = new Trend('registration_latency');
const wsSuccessCount = new Counter('ws_success_count');
const wsFailCount = new Counter('ws_fail_count');

export function enterLectureQueue(wsUrl, token, lectureId, studentId) {
  const connectStart = Date.now();
  let enterSentAt = null;

  const res = ws.connect(wsUrl, {}, (socket) => {
    socket.on('open', () => {
      const connectFrame =
        'CONNECT\n' +
        'accept-version:1.2\n' +
        'host:localhost\n' +
        'Authorization:Bearer ' + token + '\n' +
        '\n\0';

      socket.send(connectFrame);
    });

    socket.on('message', (message) => {
      if (message.startsWith('CONNECTED')) {
        wsConnectTime.add(Date.now() - connectStart);

        // 대기열 순위 알림 구독
        socket.send(
          'SUBSCRIBE\n' +
          'id:sub-rank-' + studentId + '\n' +
          'destination:/user/queue-rank\n' +
          '\n\0'
        );

        // 수강신청 완료 결과 구독
        socket.send(
          'SUBSCRIBE\n' +
          'id:sub-end-' + studentId + '\n' +
          'destination:/user/queue-end\n' +
          '\n\0'
        );

        // 대기열 입장 요청
        enterSentAt = Date.now();

        socket.send(
          'SEND\n' +
          'destination:/app/enter/' + lectureId + '\n' +
          '\n\0'
        );
      }

      // 대기열 순위 응답
      if (message.includes('destination:/user/queue-rank')) {
        if (enterSentAt) {
          wsEnterLatency.add(Date.now() - enterSentAt);
        }
      }

      // 수강신청 최종 완료 응답
      if (message.includes('destination:/user/queue-end')) {
        if (enterSentAt) {
          registrationLatency.add(Date.now() - enterSentAt);
        }

        if (
          message.includes('"success":true') ||
          message.includes('SUCCESS')
        ) {
          wsSuccessCount.add(1);
        } else {
          wsFailCount.add(1);
        }

        socket.close();
      }

      // WebSocket/STOMP 에러
      if (message.startsWith('ERROR')) {
        wsFailCount.add(1);
        socket.close();
      }
    });

    socket.on('close', () => {});

    // 40초 타임아웃
    socket.setTimeout(() => {
      wsFailCount.add(1);
      socket.close();
    }, 40000);
  });

  return res;
}