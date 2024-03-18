package chatting;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientChat {

    // Field
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String chatName;

    public static void main(String[] args) {

        try {
            ClientChat clientChat = new ClientChat();
            clientChat.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("대화명 입력 : ");
            clientChat.chatName = br.readLine();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "incoming");
            jsonObject.put("data", clientChat.chatName);
            String json = jsonObject.toString();
            clientChat.send(json);

            clientChat.receive();

            System.out.println("-------------------------------------------------------");
            System.out.println("서버를 종료하려면 'q' 또는 'Q' 를 입력하고 Enter key를 입력하세요.");
            System.out.println("-------------------------------------------------------");

            while (true) {
                String message = br.readLine();
                if(message.equals("q") || message.equals("Q")) {
                    break;
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("command", "message");
                    jsonObject.put("data", message);
                    json = jsonObject.toString();

                    clientChat.send(json);
                }
            }

            br.close();
            clientChat.unconnect();

        } catch (IOException e) {
            System.out.println("[Client] 서버 연결 안됨");
        }

        System.out.println("[Client] Stopped !!");

    }

    // Method : Connecting server
    public void connect() throws IOException {
        socket = new Socket("localhost", 50001);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("[Client] 서버에 연결됨");
    }

    // Method: Get JSON
    public void receive() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String json = dataInputStream.readUTF();
                    JSONObject root = new JSONObject(json);
                    String clientIp = root.getString("clientIp");
                    String chatName = root.getString("chatName");
                    String message = root.getString("message");
                    System.out.println("<" + chatName + "@" + clientIp + ">" + message);
                }
            } catch (IOException e) {
                System.out.println("[Client] 서버에 연결 끊김...");
                System.exit(0);
            }
        });
        thread.start();
    }

    // Method: Send JSON
    public void send(String json) throws IOException {
        dataOutputStream.writeUTF(json);
        dataOutputStream.flush();
    }

    // Method: Disconnecting Server
    public void unconnect() throws IOException {
        socket.close();
    }
}
