package chatting;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {

    // 필드
    ServerChat serverChat;
    Socket socket;
    String clientIp;
    String chatName;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    // Constructor
    public SocketClient(ServerChat serverChat, Socket socket) {

        try {
            this.serverChat = serverChat;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

            InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            this.clientIp = inetSocketAddress.getHostString();
            receive();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Method : Send JSON
    public void send(String json) {
        try {
            dataOutputStream.writeUTF(json);
            dataOutputStream.flush();
        } catch (IOException e) {

        }
    }

    // Method : Get JSON
    public void receive(){
        serverChat.threadPool.execute(()->{
            try{
                while (true){
                    // { "command" : "incoming", "data" : "chatName" }
                    // { "command" : "message", "data" : "xxxx" }
                    String receiveJson = dataInputStream.readUTF();

                    JSONObject jsonObject = new JSONObject(receiveJson);
                    String command = jsonObject.getString("command");

                    switch (command){
                        case "incoming":
                            this.chatName = jsonObject.getString("data");
                            serverChat.sendToAll(this,"가 들어오셨습니다.");
                            serverChat.addSocketClient(this);
                            break;
                        case "message":
                            String message = jsonObject.getString("data");
                            serverChat.sendToAll(this,message);
                            break;
                    }
                }
            }catch(IOException e){
                serverChat.sendToAll(this,"가 나가셨습니다.");
                serverChat.removeSocketClient(this);
            }
        });
    }

    // Method : Connection close
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {

        }
    }
}
