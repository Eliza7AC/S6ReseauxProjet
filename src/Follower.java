import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Follower {

    public static ArrayList<Integer> subscriptions;

    public static void main(String[] args) throws IOException, InterruptedException {

        subscriptions = new ArrayList<>();

        /**
         * connection to server
         * with selectable channel for stream-oriented connecting sockets
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to Server on port 12345...");
        log("--- Type nothing (just press ENTER key) to validate.");


        /**
         * asking for info to client
         */
        while(true){
            String res = askInfo("id of users you want to follow");
            if(res.equals("")){
                break;
            }
            Integer resId = Integer.parseInt(res);
            subscriptions.add(resId);
        }
        System.out.println(subscriptions.toString());

        /**
         * TODO constructing answer for server
         *  + processing request server side
         */


        /**
         * TODO sending header to server
         */
        for (Integer id : subscriptions){
            String idString = String.valueOf(id);

            byte[] message = idString.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);
            log("sending: " + idString);
            buffer.clear();

            // wait for 1 sec before sending next message
            Thread.sleep(1000);
        }


        /**
         * reading answer from server
         */
        ByteBuffer bufferReader = ByteBuffer.allocate(256);
        clientChannel.read(bufferReader);
        String result = new String(bufferReader.array()).trim();
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
