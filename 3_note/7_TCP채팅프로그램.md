## 7. TCP 채팅 프로그램

TCP 네트워킹을 이용해서 채팅 서버와 클라이언트를 구현해보자. 다음은 서버와 클라이언트에서 사용할 클래스 이름을 보여준다.


| 클래스 | 용도 |
| ------ | --- |
| ServerChat | - 채팅 서버 실행 클래스<br> - ServerSocket을 생성하고 50001에 바인딩<br> - ClientChat 연결 수락 후 SocketClient 생성 |
| SocketClient | - ClientChat과 1:1로 통신 |
| ClientChat | - 채팅 클라이언트 실행 클래스<br> - ServerChat에 연결 요청<br> - SocketClient와 1:1로 통신 |



### 채팅 서버

ServerChat 채팅 서버 실행 클래스로 클라이언트의 연결 요청을 수락하고 통신용 SocketClient를 생성하는 역할을 한다.
먼저 ServerChat 클래스의 선언부와 필드는 다음과 같다.

코드보기 : [ServerChat](https://github.com/atimaby28/Network-with-Java/blob/main/1_java/Network/src/chatting/ServerChat.java)

3개의 필드가 있는데 serverSocket은 클라리언트의 연결 요청을 수학하고, threadPool은 100개의 클라이언트가 동시에 채팅할 수 있도록 한다.
chatRoom은 통신용 SocketClient를 관리하는 동기화된 Map 컬렉션이다.

이전 코드에 이어서 start() 메소드를 작성해보자.

start() 메소드는 채팅 서버가 시작할 때 제일 먼저 호출되는 것으로, 50001번 Port에 바인딩하는 ServerSocket을 생성하고 작업 스레드가 처리할 Runnable을 람다식 () -> {...}으로 제공한다. 람다식은 accept() 메소드로 연결 수학하고, 통신용 SocketClient를 반복해서 생성한다.
다음으로 addSocketClient()와 removeSocketClient() 메소드를 작성해보자.

addSocketClient() 메소드는 연결된 클라이언트의 SocketClient를 chatRoom(채팅방)에 추가하는 역할을 한다.
키는 'chatName@clientIp'로 하고 SocketClient를 값으로 해서 저장한다.

removeSocketClient() 메소드는 연결이 끊긴 클라이언트의 SocketClient를 chatRoom(채팅방)에서 제가하는 역할을 한다.
다음으로 sendToAll() 메소드를 작성해보자.

sendToAll() 메소드는 JSON 메시지를 생성해 채팅방에 있는 모든 클라이언트에게 보내는 역할을 한다. JSON 메시지는 다음과 같은 구조로 되어 있다.

chatRoom.values()로 Collection<SocketClient>를 얻은 후 모든 SocketClient로 하여금 send() 메소드로 JSON 메시지를 보내게 하였다.
단, 메시지를 보낸 SocketClient는 제외한다. 다음으로 stop() 메소드를 작성해보자.

stop() 메소드는 채팅 서버를 종료시키는 역할을 한다. serverSocket과 threadPool을 닫고 chatRoom에 있는 모든 SocketClient를 닫는다.
그리고 chatRoom.values()로 Collection<SocketClient>를 얻고, 요소 스트림을 이용해서 전체 SocketClient의 close() 메소드를 호출한다.
마지막으로 main() 메소드를 작성해보자.

main() 메소드는 채팅 서버를 시작하기 위해 ServerChat 객체를 생성하고 start() 메소드를 호출한다.
키보드로 q를 입력하면 stop() 메소드를 호출해서 채팅 서버를 종료한다.
이제 SocketClient 클래스의 선언부와 필드를 선언해보자.

코드보기 : [SocketClient](https://github.com/atimaby28/Network-with-Java/blob/main/1_java/Network/src/chatting/SocketClient.java)

SocketClient는 클라이언트와 1:1로 통신하는 역할을 한다. ServerChat 필드는 ServerChat()의 메소드를 호출하기 위해 필요하다. socket은 연결을 끊을 때 필요하고, dis와 dos는 문자열을 읽고 보내기 위한 보조 스트림이다. clientIP와 chatName은 클라이언트 IP 주소와 대화명이다.

매개값으로 받은 ServerChat Socket을 필드에 저장한 다음 문자열 입출력을 위해 DataInputStream과 DataOutputStream을 생성해서 필드에 저장한다.
그리고 클라이언트의 주소를 필드에 저장한다. 마지막으로 receive() 메소드를 호출한다.
다음으로 receive() 메소드를 작성해보자.

receive() 메소드는 클라이언트가 보낸 JSON 메시지를 읽는 역할을 한다. dis.readUTF()로 JSON을 읽고 JSONObject로 파싱해 command 값을 먼저 얻어낸다. 그 이유는 command에 따라 처리 내용이 달라지기 때문이다.

command가 incomming이라면 JSON에서 대화명을 읽고 ChatRoom에 SocketClient를 추가한다. command가 message라면 JSON에서 메시지를 읽고 연결되어 있는 모든 클라이언트에게 보낸다. 클라리언트가 채팅을 종료할 경우 dis.readUTF()에서 IOException이 발생하기 때문에, 예외 처리를 해서 chatRoom에 저장되어 있는 SocketClient를 제거한다.

다음으로 send() 메소드를 작성해보자.

send() 메소드는 연결된 클라이언트로, JSON 메시지를 보내는 역할을 한다. ClientServer의 sendToAll() 메소드에서 호출된다.
다음으로 close() 메소드를 작성해보자.

close() 메소드는 클라이언트와 연결을 끊는 역할을 한다. ServerChat stop() 메소드에서 호출된다.
ServerChat SocketClient 클래스를 모두 작성했다면 ServerChat 실행해보자.



### 채팅 클라이언트

채팅 클라이언트는 ClientChat 단일 클래스이다. ClientChat는 채팅 서버로 연결을 요청하고, 연결된 후에는 제일 먼저 대화명을 보낸다. 그리고 난 다음 서버와 메시지를 주고 받는다. 먼저 ClientChat 클래스의 선언부와 필드를 다음과 같이 작성해보자.

코드보기 : [ClientChat](https://github.com/atimaby28/Network-with-Java/blob/main/1_java/Network/src/chatting/ClientChat.java)

socket은 연결 요청과 연결을 끊을 때 필요하고, dis와 dos는 문자열을 읽고 보내기 위한 보조 스트림이다. chatName은 클라이언트의 대화명이다.
다음으로 connect() 메소드를 작성해보자.

connect() 메소드는 채팅 서버 (localhost, 50001)에 연결 요청을 하고 Socket을 필드에 저장한다. 그리고 문자열 입출력을 위해 DataInputStream과 DataOutputStream을 생성해서 필드에 저장한다. 만약 다른 PC에 있는 채팅 서버와 연결을 하고 싶다면 localhost 대신 IP 주소로 변경하면 된다.

다음으로 receive() 메소드를 작성해보자.

receive() 메소드는 서버가 보낸 JSON 메시지를 읽는 역할을 한다. dis.readUTF()로 JSON을 읽고 JSONObject로 파싱해서 clientIP, chatName, message를 얻어낸다. 그리고 Console 뷰에 "<chatName@clientIp> message"로 출력한다.

서버와 통신이 끊어지면 dis.readUTF()에서 IOException이 발생하기 때문에, 예외 처리를 해서 클라이언트도 종료되도록 한다.
다음으로 send() 메소드를 작성해보자.

send() 메소드는 서버로 JSON 메시지를 보내는 역할을 한다. main() 메소드에서 키보드로 입력한 메시지를 보낼 때 호출된다.

다음으로 unconnect() 메소드를 작성해보자.

unconnect() 메소드는 Socket의 close() 메소드를 호출해서 서버와 연결을 끊는다. main() 메소드에서 q가 입력되었을 때 채팅을 종료하기 위해 호출된다.
다음으로 main() 메소드를 작성해보자.

main() 메소드는 채팅 클라이언트를 시작하기 위해 ClientChat 객체를 생성하고, 채팅 서버와 연결하기 위해 connect() 메소드를 호출한다. 연결이 되면 대화명을 키보드로부터 입력받고 다음과 같은 JSON 메시지를 서버로 보낸다.

다음으로 채팅 서버에서 보내는 메시지를 받기 위해 receive() 메소드를 호출하고, 사용자가 키보드로 메시지를 입력하면 다음과 같은 JSON 메시지를 생성해서 서버로 보낸다.

만약 사용자가 q를 입력하면 unconnect() 메소드를 호출하고 클라리언트를 종료한다.
이제 ClientChat 실행해서 채팅을 해보자. 먼저 ServerChat 다음과 같이 실행한다.

마지막으로 ClientChat 프로세스를 하나씩 종료시켜보자. ServerChat 실행하고 있는 Console 뷰에는 채팅방을 나간 사용자 정보와 현재 채팅자 수가 출력된다.
