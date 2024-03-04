package chatting;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerChat {

    // Field
    ServerSocket serverSocket;
    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    // Multi-thread 환경에서는 HashMap 보다는 Hashtable을 사용한다.
    // Map<String, SocketClient> chatRoom = new Hashtable<>();
    // 아니면 synchronizedMap을 사용해 본다.
    Map<String, SocketClient> chatRoom = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {

        try {
            ServerChat serverChat = new ServerChat();
            serverChat.start();

            System.out.println("-------------------------------------------------------");
            System.out.println("서버를 종료하려면 'q' 또는 'Q' 를 입력하고 Enter key를 입력하세요.");
            System.out.println("-------------------------------------------------------");

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
            serverChat.stop();
        } catch (IOException e) {
            System.out.println("[Server] " + e.getMessage());
        }

    }

    // Method : Start Server
    public void start() throws IOException {
        serverSocket = new ServerSocket(50001);
        System.out.println("[Server] Started...");

        Thread thread = new Thread(() -> {
           try {
               while (true) {
                   Socket socket = serverSocket.accept();
                   SocketClient socketClient = new SocketClient(this, socket);
               }
           } catch (Exception e) {

           }
        });
        thread.start();
    }

    // Method : Add SocketClient when connecting client
    public void addSocketClient(SocketClient socketClient) {
        String key = socketClient.chatName + "@" + socketClient.clientIp;
        chatRoom.put(key, socketClient);

        System.out.println("입장 : " + key);
        System.out.println("현재 채팅자 수 : " + chatRoom.size() + "\n");
    }

    // Method : Remove SocketClient when disconnecting client
    public void removeSocketClient(SocketClient socketClient) {
        String key = socketClient.chatName + "@" + socketClient.clientIp;
        chatRoom.remove(key);

        System.out.println("퇴장 : " + key);
        System.out.println("현재 채팅자 수 : " + chatRoom.size() + "\n");
    }

    // Method : Broadcast to all client
    public void sendToAll(SocketClient sender, String message) {
        JSONObject root = new JSONObject();

        root.put("clientIp", sender.clientIp);
        root.put("chatName", sender.chatName);
        root.put("message", message);

        String json = root.toString();

        Collection<SocketClient> socketClients = chatRoom.values();
        for (SocketClient socketClient : socketClients) {
            if(socketClient == sender) {
                continue;
            }

            socketClient.send(json);
        }
    }

    // Method : Server shutdown
    public void stop() {
        try {
            serverSocket.close();
            threadPool.shutdown();
            chatRoom.values().stream().forEach(socketClient -> socketClient.close());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[Server] Shutdown !!");
    }

}
