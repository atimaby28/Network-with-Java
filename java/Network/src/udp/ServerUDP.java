package udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class ServerUDP {

    private static DatagramSocket datagramSocket;

    public static void main(String[] args) throws IOException {
        System.out.println("-------------------------------------------------------");
        System.out.println("서버를 종료하려면 'q' 또는 'Q' 를 입력하고 Enter key를 입력하세요.");
        System.out.println("-------------------------------------------------------");

        // UDP 서버 시작
        startServer();

        // 키보드 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String key = br.readLine();
            if(key.equals("q") || key.equals("Q")) {
                break;
            }
        }

        br.close();

        // UDP 서버 종료
        stopServer();

    }

    private static void startServer() {
        // 작업 스레드 정의
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // DatagramSocket 생성 및 Port Binding
                    datagramSocket = new DatagramSocket(50001);
                    System.out.println("[Server] Started...");

                    while (true) {
                        // 클라이언트가 구독하고 싶은 뉴스 주제 얻기
                        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);

                        System.out.println("\n클라이언트의 희망 뉴스 종류를 얻기 위해 대기함...\n");
                        datagramSocket.receive(receivePacket);
                        String newsKind = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

                        // 클라이언트의 IP와 Port 정보가 있는 SocketAddress 얻기
                        SocketAddress socketAddress = receivePacket.getSocketAddress();

                        for (int i = 1; i <= 10; i++) {
                            String data = newsKind + " : News" + i;
                            byte[] bytes = data.getBytes("UTF-8");
                            DatagramPacket sendPacket = new DatagramPacket(bytes, 0, bytes.length, socketAddress);
                            datagramSocket.send(sendPacket);
                            Thread.sleep(1000);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[Server] " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // 스레드 시작
        thread.start();
    }

    private static void stopServer() {
        datagramSocket.close();
        System.out.println("[Server] Stopped !!");
    }

}
