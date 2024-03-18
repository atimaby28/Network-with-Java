package tcp_echo_thread;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThreadEcho {

    private static ServerSocket serverSocket;
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        System.out.println("-------------------------------------------------------");
        System.out.println("서버를 종료하려면 'q' 또는 'Q' 를 입력하고 Enter key를 입력하세요.");
        System.out.println("-------------------------------------------------------");

        // TCP 서버 시작
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

        // TCP 서버 종료
        stopServer();

    }

    private static void startServer() {
        // 작업 스레드 정의
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // ServerSocket 생성 및 Port 바인딩
                    serverSocket = new ServerSocket(50001);
                    System.out.println("[Server] Started...");

                    while (true) {
                        System.out.println("\n[Server] Waiting for connection...\n");
                        // 연결 수락
                        Socket socket = serverSocket.accept();

                        executorService.execute(() -> {
                            try {
                                // 연결된 클라이언트 정보 얻기
                                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();

                                String clientIP = isa.getHostString();
                                System.out.println("\n[Server] " + clientIP + "의 연결 요청을 수락함\n");

                                // =======================================================================

                                // 데이터 받기
                                DataInputStream dis = new DataInputStream(socket.getInputStream());
                                String message = dis.readUTF();

                                // 데이터 보내기
                                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeUTF(message);
                                dos.flush();

                                System.out.println("[Server] 받은 데이터를 다시 보냄 : " + message + "\n");

                                // =======================================================================

                                // 연결 끊기
                                socket.close();
                                System.out.println("[Server] " + clientIP + "의 연결을 끊음");

                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                } catch (IOException e) {
                    System.out.println("[Server] " + e.toString());
                    e.printStackTrace();
                }
            }
        };

        // 스레드 시작
        thread.start();

    }

    private static void stopServer() throws IOException {

        // Server Socket을 닫고 Port Unbinding
        serverSocket.close();
        executorService.shutdownNow();
        System.out.println("[Server] Shutdown");

    }
}
