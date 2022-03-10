import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Repost {

    public static ArrayList<Integer> usersFollowed;

    public static void main(String[] args) throws IOException {
        /**
         * connection to server
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to Server on port 12345...");

        /**
         * asking for info to client
         */
        usersFollowed = new ArrayList<>();
        while(true) {
            /**
             * constructing answer for server
             *  + processing request server side
             *  THE REQUEST MUST ENDED WITH "END" WORD
             */
            String userId = askInfo("id");
            if (userId.equals("")) {
                break;
            }

            String header = "REPUBLISH author:@" + userId + " msg_id:" + userId; // REPUBLISH author:@user msg_id:id END
            String answer = header + Request.END;


            /**
             * sending header to server
             */
            byte[] message = answer.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);
            log("sending: " + answer);
            buffer.clear();


            /**
             * reading answer from server
             */
            ByteBuffer bufferReader = ByteBuffer.allocate(256);
            clientChannel.read(bufferReader);
            String result = new String(bufferReader.array()).trim();
            log("--- answer from server : " + result);

        }

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
