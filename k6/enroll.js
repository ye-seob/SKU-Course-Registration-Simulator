import ws from 'k6/ws';

export function enterLectureQueue(wsUrl, token, lectureId, studentId) {
  const res = ws.connect(wsUrl, {}, (socket) => {
    socket.on('open', () => {
      const connectFrame =
        'CONNECT\n' +
        'accept-version:1.2\n' +
        'host:localhost\n' +
        'Authorization:Bearer ' + token +
        '\n' +
        '\n' +
        '\0';
      socket.send(connectFrame);
    });

    socket.on('message', (message) => {

      if (message.startsWith('CONNECTED')) {

        const enterFrame =
          'SEND\n' +
          'destination:/app/enter/' + lectureId +
          '\n' +
          '\n' +
          '\0';


        socket.send(enterFrame);
      }

      if (message.startsWith('ERROR')) {
        socket.close();
      }
    });

    socket.on('close', () => {
    });

    socket.setTimeout(() => {
      socket.close();
    }, 10000);
  });

  return res;
}