package ClientSide;

import request.RequestClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;


/**
 * Design Pattern : Template method
 *
 * Client class => defines the first step (connection to server)
 * Descendant class => defines intermediate steps (info retrieved from user)
 * Client class => defines last steps (sending data to server and closing connection)
 */
public abstract class Client {

    protected SocketChannel clientChannel;
    protected String answer;

    /** constructor = connection to server */
    public Client() throws IOException {
        this.clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to ServerSide.Server on port 12345...");
    }

    public void sendToServer(RequestClient requestClient) throws IOException, InterruptedException {
        for (String s : requestClient.getInfo()){
            byte[] message = s.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);
            Client.log("sending: " + s);
            buffer.clear();

            // wait for 1 sec before sending next message
            Thread.sleep(1000);
        }
    }

    public void readingFromServer(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        answer = new String(buffer.array()).trim();
        log("--- answer from server : " + answer);

    }


    public void disconnectFromServer(SocketChannel clientChannel) throws IOException {
        clientChannel.close();
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    protected static void log(String str) {
        System.out.println(str);
    }

    protected static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public String getAnswer() {
        return answer;
    }
}
