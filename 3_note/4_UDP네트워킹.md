## 4. UDP 네트워킹

UDP(User Datagram Protocol)는 발산자가 일방적으로 수신자에게 데이터를 보내는 방식으로, TCP처럼 연결 요청 및 수락 과정이 없기 때문에 TCP보다 데이터 전송 속도가 상대적으로 빠르다.

UDP는 TCP처럼 고정 회선이 아니라 여러 회선을 통해 데이터가 전송되기 때문에 특정 회선의 속도에 따라 데이터가 순서대로 전달되지 않거나 잘못된 회선으로 인해 데이터 손실이 발생할 수 있다. 하지만 실시간 영상 스트리밍에서 한 컷의 영상이 손실되더라도 영상은 계속해서 수신되므로 문제가 되지는 않는다. 

따라서 데이터 전달의 신뢰성보다 속도가 중요하다면 UDP를 사용하고, 데이터 전달의 신뢰성이 중요하다면 TCP를 사용해야 한다.

자바는 UDP 네트워킹을 위해 java.net 패키지에서 DatagramSocket과 DatagramPacket 클래스를 제공하고 있다.
DatagramSocket은 발신점과 수신점에 해당하고 DatagramPacket은 주고받는 데이터에 해당한다.

![](../images/img10.png)



### UDP 서버

UDP 서버를 위한 DatagramSocket 객체를 생성할 때에는 다음과 같이 바인딩할 Port 번호를 생성자 매개값으로 제공해야 한다.

> DatagramSocket datagramSocket = new DatagramSocket(50001);

UDP 서버는 클라이언트가 보낸 DatagramPacket을 항상 받을 준비를 해야한다.
이 역할을 하는 메소드가 receive()이다. receive() 메소드는 데이터를 수신할 때까지 블로킹되고, 데이터가 수신되면 매개값으로 주어진 DatagramPacket에 저장한다.

> DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
> datagramSocket.receive(receivePacket);

DatagramPacket 생성자의 첫 번째 매개값은 수신된 데이터를 저장할 배열이고 두 번째 매개값은 수신할 수 있는 최대 바이트 수이다.
보통 첫 번째 바이트 배열의 크기를 준다. receive() 메소드가 실행된 후 수신된 데이터와 바이트 수를 얻는 방법은 다음과 같다.

> byte[] bytes = receivePacket.getData();
> int num = receivePacket.getLength();

읽은 데이터가 문자열이라면 다음과 같이 String 생성자를 이용해서 문자열을 얻을 수 있다.

> String data = new String(bytes, 0, num, "UTF-8");

이제 반대로 UDP 서버가 클라이언트에게 처리 내용을 보내려면 클라리언트 IP 주소와 Port 번호가 필요한데, 이것은 receive()로 받은 DatagramPacket에서 얻을 수 있다. getSocketAddress() 메소드를 호출하면 정보가 담긴 SocketAddress 객체를 얻을 수 있다.

> SocketAddress socketAddress = receivePacket.getSocketAddress();

이렇게 얻은 SocketAddress 객체는 다음과 같이 클라이언트로 보낼 DatagramPacket을 생성할 때 네 번째 매개값으로 사용된다. DatagramPacket 생성자의 첫 번째 매개값은 바이트 배열이고 두 번째는 시작 인덱스, 세 번째는 보낼 바이트 수이다.

``` Java
String data = "처리 내용";
byte[] bytes = data.getBytes("UTF-8");
DatagramPacket sendPacket = new DatagramPacket(bytes, 0, bytes.length, socketAddress);
```

DatagramPacket을 클라이언트로 보낼 때는 DatagramSocket의 send() 메소드를 이용한다.

> datagramSocket.send(sendPacket);

더 이상 UDP 클라리언트의 데이터를 수신하지 않고 UDP 서버를 종료하고 싶을 경우에는 다음과 같이 DatagramSocket의 close() 메소드를 호출하면 된다.

> datagramSocket.close();

다음 예제는 UDP 클라이언트가 구독하고 싶은 뉴스 10개를 전송하는 UDP 서버이다.

코드보기 : [ServerUDP.java](https://github.com/atimaby28/Network-with-Java/blob/main/1_java/Network/src/udp/ServerUDP.java)



### UDP 클라이언트

UDP 클라이언트는 서버에 요청 내용을 보내고 그 결과를 받는 역할을 한다. UDP 클라리언트를 위한 DatagramSocket 객체는 기본 생성자로 생성한다.
Port 번호는 자동으로 부여되기 때문에 따로 지정할 필요가 없다.

> DatagramSocket datagramSocket = new DatagramSocket();

요청 내용을 보내기 위한 DatagramPacket을 생성하는 방법은 다음과 같다.

``` Java
String data = "요청 내용";
byte[] bytes = data.getBytes("UTF-8");
DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, new InetSocketAddress("localhost", 50001)); 
```

DatagramPacket 생성자의 첫 번째 값은 바이트 배열이고, 두 번째 매개값은 바이트 배열에서 보내고자 하는 바이트 수이다. 세 번째 매개값은 UDP 서버의 IP와 Port 정보를 가지고 있는 InetSocketAddress 객체이다. 

생성된 DatagramPacket을 매개값으로해서 DatagramSocket의 send() 메소드를 호출하면 UDP 서버로 DatagramPacket이 전송된다.

> datagramSocket.send(sendPacket);

UDP 서버에서 처리 결과가 언제 올지 모르므로 항상 받을 준비를 하기 위해 receive() 메소드를 호출한다. receive() 메소드는 데이터를 수신할 때까지 블로킹되고, 데이터가 수신되면 매개값으로 주어진 DatagramPacket에 저장한다. 이 부분은 UDP 서버와 동일하다. 더 이상 UDP 서버와 통신할 필요가 없다면 DatagramSocket을 닫기 위해 close() 메소드를 다음과 같이 호출한다.

> datagramSocket.close();

다음은 이전 예제인 NewServer로 구독하고 싶은 뉴스 주제를 보내고 관련 뉴스 10개를 받는 UDP 클라이언트이다.

코드보기 : [ClientUDP.java](https://github.com/atimaby28/Network-with-Java/blob/main/1_java/Network/src/udp/ClientUDP.java)

