import java.io.IOException;
import java.lang.reflect.Array;
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
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 1234));
        log("--- Connecting to Server on port 1234...");

       // INFO - while
//        log("--- Please, type \"exit\" to quit.\n");



        /**
         * ask for info to create request
         * @type (once)
         * @user (once)
         * @body (multiple times)
         */
        String user = askInfo("USER");


//        while(userIsConnected()){ // INFO - while

            String type = askInfo("TYPE"); // for example: PUBLISH
            String body = askInfo("BODY")+"END";
            Request request = new Request(type, user, body);

//            if (body.equals("exit")){ break; } // INFO - while

            /**
             * sending info of request to server
             */
            for (String s : request.getInfos()){
                byte[] message = s.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                clientChannel.write(buffer);
                log("sending: " + s);
                buffer.clear();

                // wait for 1 sec before sending next message
                Thread.sleep(1000);
            }

            /**
             * sum-up of the request sent
             */
            printRequest(request);

//        } // INFO - while



//        SocketChannel readableChannel = (SocketChannel) .channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        String result = new String(buffer.array()).trim();
        System.out.println("result : " + result);


        // close(): Closes this channel.
        // If the channel has already been closed then this method returns immediately.
        // Otherwise it marks the channel as closed and then invokes the implCloseChannel method in order to complete the close operation.
        clientChannel.close();

    }




    /**
     * auxiliaries methods
     * @param str
     */
    private static void log(String str) {
        System.out.println(str);
    }

    public static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

//    public static String askForUser(){
//        System.out.print("User > ");
//        Scanner sc = new Scanner(System.in);
//        return sc.nextLine();
//    }
//
//    public static String askForBody(){
//        System.out.print("Body (msg) > ");
//        Scanner sc = new Scanner(System.in);
//        return sc.nextLine();
//    }

    public static String requestToString(Request request){
         return new String(
                 "### " + request.getHeader() + "\n" +
                 "### " + request.getBody() + "\n");
    }

    public static void printRequest(Request request){
        System.out.println(requestToString(request));
    }

    public static boolean userIsConnected(){
        return true;
    }

    public static void send(String infoToSend, SocketChannel clientChannel) throws IOException {
        byte[] message = infoToSend.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        clientChannel.write(buffer);
//        log("sending: " + infoToSend);
        buffer.clear();
    }



}
