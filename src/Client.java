import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    /**
     * types:
     * sent from clients: PUBLISH, REPLY, REPUBLISH, (UN)SUBSCRIBE
     * sent from server: OK, ERROR
     */
    public static String type; // PUBLISH
    public static String user; // @user
    public static String header; // PUBLISH author:@user
    public static String body;

    public static ArrayList<String> request = new ArrayList<>();


    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * connection to server
         * with selectable channel for stream-oriented connecting sockets
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 1234));
        log("--- Connecting to Server on port 1234...");


        /**
         * ask for info to create request
         * @type (once)
         * @user (once)
         * @body (multiple times)
         */
        String type = askInfo("TYPE"); // for example: PUBLISH

        if (type.equals("PUBLISH")){
            user = askInfo("USER");
            body = askInfo("BODY")+"END";
            header = type.toUpperCase()+" "+"author:@"+user;

            request.add(header);
            request.add(body);
        }

        // TODO
        else if (type.equals("RCV_IDS")){
            user = askInfo("USER");
            body = askInfo("BODY")+"END";
            header = type.toUpperCase()+" "+"author:@"+user;

        }


//        String user = askInfo("USER");
//        String body = askInfo("BODY")+"END";
//        Request request = new Request(type, user, body);



        /**
         * sending info of request to server
         */
        for (String s : request){
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
        System.out.println(request.toString());
//        printRequest(request);



        /**
         * reading answer from server
         */
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        String result = new String(buffer.array()).trim();
        System.out.println("result : " + result);


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

    public static String createHeader(){
        if (type.equals("PUBLISH")){
            return type.toUpperCase()+" "+"author:@"+user;
        }
        return "ERROR HEADER";
    }



}
