import ws from 'k6/ws';
import { Trend, Counter } from 'k6/metrics';

// WebSocket 연결 응답까지 걸린 시간
const wsConnectTime = new Trend('ws_connect_time');

// STOMP CONNECT 응답까지 걸린 시간
const stompConnectTime = new Trend('stomp_connect_time');

// 대기열 입장 요청 → 첫 queue-rank 응답까지 걸린 시간
const queueEnterLatency = new Trend('queue_enter_latency');

// 대기열 입장 요청 → 최종 수강신청 완료까지 걸린 전체 시간
const registrationLatency = new Trend('registration_latency');

// 대기열 입장 요청 누적 횟수
const queueEnteredCounter = new Counter('queue_entered_total');

// 수강신청 완료 누적 횟수
const registrationCompletedCounter = new Counter(
  'registration_completed_total'
);

export function enterLectureQueue(
  wsUrl,
  token,
  lectureId,
  studentId
) {
  // WebSocket 연결 시작 시간
  const connectStartAt = Date.now();

  // WebSocket 연결 완료 시간
  let wsOpenedAt = null;

  // STOMP CONNECT 요청을 보낸 시간
  let stompConnectSentAt = null;

  // 대기열 입장 요청을 보낸 시간
  let enterSentAt = null;

  // CONNECTED 응답 중복 처리 방지
  let stompConnected = false;

  // 첫 queue-rank 응답만 측정
  let rankReceived = false;

  // 최종 완료 여부
  let registrationCompleted = false;

  // 종료 처리 중복 방지
  let socketClosed = false;

  let res;

  try {
    res = ws.connect(wsUrl, {}, (socket) => {

      // WebSocket 연결 성공
      socket.on('open', () => {
        wsOpenedAt = Date.now();

        // WebSocket 연결 시간 측정
        wsConnectTime.add(
          wsOpenedAt - connectStartAt
        );

        // STOMP CONNECT 요청 시간
        stompConnectSentAt = wsOpenedAt;

        const connectFrame =
          'CONNECT\n' +
          'accept-version:1.2\n' +
          'host:localhost\n' +
          'Authorization:Bearer ' + token + '\n' +
          '\n\0';

        socket.send(connectFrame);
      });

      // WebSocket 오류
      socket.on('error', (error) => {
        console.error(error)
      });

      // 메시지 수신
      socket.on('message', (raw) => {
        const frames = raw
          .split('\0')
          .filter(
            (frame) => frame.trim().length > 0
          );

        frames.forEach((message) => {

          // STOMP CONNECTED
          if (message.startsWith('CONNECTED')) {

            if (stompConnected) {
              return;
            }

            stompConnected = true;

            const connectedAt = Date.now();

            // STOMP CONNECT → CONNECTED
            if (stompConnectSentAt) {
              stompConnectTime.add(
                connectedAt - stompConnectSentAt
              );
            }

            // queue-rank 구독
            socket.send(
              'SUBSCRIBE\n' +
              'id:sub-rank-' + studentId + '\n' +
              'destination:/user/queue-rank\n' +
              '\n\0'
            );

            // queue-end 구독
            socket.send(
              'SUBSCRIBE\n' +
              'id:sub-end-' + studentId + '\n' +
              'destination:/user/queue-end\n' +
              '\n\0'
            );

            // 대기열 입장 요청 시작
            enterSentAt = Date.now();

            queueEnteredCounter.add(1);

            socket.send(
              'SEND\n' +
              'destination:/app/enter/' + lectureId + '\n' +
              '\n\0'
            );
          }

          // 첫 queue-rank 응답
          if (
            message.includes(
              'destination:/user/queue-rank'
            )
          ) {
            if (
              enterSentAt &&
              !rankReceived
            ) {
              rankReceived = true;

              queueEnterLatency.add(
                Date.now() - enterSentAt
              );
            }
          }

          // 최종 수강신청 완료
          if (
            message.includes(
              'destination:/user/queue-end'
            )
          ) {
            if (registrationCompleted) {
              return;
            }

            registrationCompleted = true;

            const completedAt = Date.now();

            if (enterSentAt) {
              registrationLatency.add(
                completedAt - enterSentAt
              );

              registrationCompletedCounter.add(1);
            }

            socket.close();
          }

          // STOMP ERROR
          if (message.startsWith('ERROR')) {
            console.error(message);

            socket.close();
          }
        });
      });

      // WebSocket 종료
      socket.on('close', () => {
        socketClosed = true;
      });

      // 최대 40초 대기
      socket.setTimeout(() => {
        if (!socketClosed) {
          socket.close();
        }
      }, 40000);
    });

  } catch (error) {
    console.error(error)
  }

  return res;
}