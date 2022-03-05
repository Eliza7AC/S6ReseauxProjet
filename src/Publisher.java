import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Publisher {

    /**
     * types:
     * sent from clients: PUBLISH, REPLY, REPUBLISH, (UN)SUBSCRIBE
     * sent from server: OK, ERROR
     */
    public static String type; // PUBLISH
    public static String user; // @user
    public static String header; // PUBLISH author:@user
    public static String body;

    public static String optionUser = "";
    public static String optionTag = "";
    public static String optionId = "";
    public static String optionLimit = "";


    public static ArrayList<String> request = new ArrayList<>();


    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * connection to server
         * with selectable channel for stream-oriented connecting sockets
         */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to Server on port 12345...");


        /**
         * ask for info to create request
         */
        String type = askInfo("TYPE"); // for example: PUBLISH

        if (type.equals("PUBLISH")){
            user = askInfo("USER");
            header = type.toUpperCase()+" "+"author:@"+user;
            body = askInfo("BODY")+"END";

            request.add(header);
            request.add(body);
        }
        else if (type.equals("RCV_IDS")){
            optionUser = askInfo("(option) user");
            optionTag = askInfo("(option) tag");
            optionId = askInfo("(option) id");
            optionLimit = askInfo("(option) limit");

            header = type.toUpperCase()+" ";

            if (!optionUser.isEmpty()){
                header = header + "author:@"+optionUser + " ";
            }
            if (!optionTag.isEmpty()){
                header = header + "tag:#" + optionTag + " ";
            }
            if (!optionId.isEmpty()){
                header = header + "since_id:" + optionId + " ";
            }
            if (!optionLimit.isEmpty()){
                header = header + "limit:" + optionLimit + " ";
            }
            header = header + "END";
            request.add(header);
            System.out.println(">>>" + header);
        }
        else if (type.equals("RCV_MSG")){
            optionId = askInfo("(option) id");
            header = "RCV_MSG" + "since_id:" + optionId + "END";
        }



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
         * sum-up of the request sent and clearing request
         */
        System.out.println(request.toString());
//        printRequest(request);
        request.clear();



        /**
         * reading answer from server
         */
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        String result = new String(buffer.array()).trim();
        System.out.println("result : " + result);
        System.out.println(" ");

        if (result.equals("END")){
            clientChannel.close();
        }



    }







    private static void log(String str) {
        System.out.println(str);
    }

    public static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

//    public static String requestToString(Request request){
//         return new String(
//                 "### " + request.getHeader() + "\n" +
//                 "### " + request.getBody() + "\n");
//    }

//    public static void printRequest(Request request){
//        System.out.println(requestToString(request));
//    }

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
