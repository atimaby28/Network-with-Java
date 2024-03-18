package tcp_echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.UnexpectedException;

public class ClientEcho {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 50001);
            System.out.println("\n[Client] Connected...\n");

            // =======================================================================

            // 데이터 보내기
            String sendMessage = "안녕하세요, 네트워크 프로그래밍 연습 중입니다...";
            OutputStream outputStream = socket.getOutputStream();

            byte[] bytes = sendMessage.getBytes("UTF-8");
            outputStream.write(bytes);
            outputStream.flush();
            System.out.println("\n[Client] 데이터 보냄 : " + sendMessage + "\n");

            // 데이터 받기
            InputStream inputStream = socket.getInputStream();
            bytes = new byte[1024];

            int readByCount = inputStream.read(bytes);
            String receiveMessage = new String(bytes, 0, readByCount, "UTF-8");
            System.out.println("[Client] 데이터 받음 : " + receiveMessage);

            // =======================================================================

            socket.close();
            System.out.println("\n[Client] Disconnected !!\n");
        } catch (UnexpectedException e) {
            // IP 또는 Domain 표기 방법이 잘못되었을 경우
            System.out.println("UnexpectedException : " + e.toString());
        } catch (IOException e) {
            // IP 또는 Port Number가 존재하지 않을 경우
            System.out.println("IOException : " + e.toString());
        }
    }
}
