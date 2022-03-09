import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Follower {

    /**
     * users id that follower wants to follow
     */
    public static ArrayList<String> subscriptions;


    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * connection to server
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to Server on port 12345...");
        log("--- Type nothing (just press ENTER key) to validate.");


        /**
         * asking for info to client
         */
        subscriptions = new ArrayList<>();
        while(true){
            String user = askInfo("user you want to follow");
            if(user.equals("")){
                break;
            }
//            Integer resId = Integer.parseInt(res);
            subscriptions.add(user);
        }
        System.out.println(subscriptions.toString());



        /**
         * TODO constructing answer for server
         *  + processing request server side
         */
        String answer = "FOLLOWER ";
        for(String user: subscriptions){
            answer = answer+user+" ";
        }
        answer = answer+"END";
        System.out.println("answer ===> " + answer);


        /**
         * TODO sending header to server
         */
        byte[] message = answer.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        clientChannel.write(buffer);
        log("sending: " + answer);
        buffer.clear();

        // wait for 1 sec before sending next message
        Thread.sleep(1000);

//        for (String id : subscriptions){
//            String idString = String.valueOf(id);
//
//            byte[] message = idString.getBytes();
//            ByteBuffer buffer = ByteBuffer.wrap(message);
//            clientChannel.write(buffer);
//            log("sending: " + idString);
//            buffer.clear();
//
//            // wait for 1 sec before sending next message
//            Thread.sleep(1000);
//        }


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
