package tcp;

import java.io.IOException;
import java.net.Socket;
import java.rmi.UnexpectedException;

public class ClientTCP {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 50001);
            System.out.println("[Client] Connected...");

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
