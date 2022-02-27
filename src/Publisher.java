import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Publisher {


    public static void main(String[] args) throws IOException, InterruptedException {

        String type = "PUBLISH";
        String user = askForUser();
        String body = askForBody();
        Request request = new Request(type, user, body);


        System.out.println("#########");
        System.out.println(request.getHeader());
        System.out.println(body);
        System.out.println("#########");


        //  selectable channel for stream-oriented connecting sockets
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 1234));

        log("Connecting to Server on port 1234...");

        for (String s : request.getInfos()){
            byte[] message = s.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);


            log("sending: " + s);
            buffer.clear();

            // wait for 2 seconds before sending next message
            Thread.sleep(2000);
        }


        // close(): Closes this channel.
        // If the channel has already been closed then this method returns immediately.
        // Otherwise it marks the channel as closed and then invokes the implCloseChannel method in order to complete the close operation.
        clientChannel.close();
    }



    private static void log(String str) {
        System.out.println(str);
    }

    public static String askForUser(){
        System.out.print("votre pseudo: ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public static String askForBody(){
        System.out.print("votre msg: ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }



}
