import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Publisher {


    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * connection to server
         * with selectable channel for stream-oriented connecting sockets
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to Server on port 12345...");


        /**
         * sending request data to server
         */
        Request request = new Request();


        for (String s : request.getData()){
            byte[] message = s.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);
            log("sending: " + s);
            buffer.clear();

            // wait for 1 sec before sending next message
            Thread.sleep(1000);
        }


        /**
         * reading answer from server
         */
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        String result = new String(buffer.array()).trim();
        log("--- answer from server : " + result);
        clientChannel.close();

    }




    private static void log(String str) {
        System.out.println(str);
    }

    public static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }



}
