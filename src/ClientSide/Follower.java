package ClientSide;

import request.RCV_IDS;
import request.RCV_MSG;
import request.RequestClient;

import java.io.IOException;
import java.util.ArrayList;


public class Follower extends Client{

    private static ArrayList<String> idMsg; // user id followed

    public Follower() throws IOException {
        super();
        idMsg = new ArrayList<>();
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        /** connection to server */
        Client client = new Follower();
        log("--- Type nothing (just press ENTER key) to validate.");


        /**
         * 1 - retrieving id of msg --- RCV_IDS
         */
        RequestClient requestClient = new RCV_IDS();
        client.sendToServer(requestClient);
        client.readingFromServer(client.getClientChannel());


        /**
         * 2 - iterating through different id to get msg content --- RCV_MSG
         */
        String[] id = client.answer.split(" "); //ex: RCV_IDS | 0 | 7
        for (String i : id){
            if (RCV_MSG.isNumeric(i)){
                RequestClient requestClient2 = new RCV_MSG(i);
                client.sendToServer(requestClient2);
                client.readingFromServer(client.getClientChannel());
            }
        }


        /** disconnect */
        client.disconnectFromServer(client.getClientChannel());

    }






//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        /**
//         * connection to server
//         */
//        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
//        log("--- Connecting to ServerSide.Server on port 12345...");
//        log("--- Type nothing (just press ENTER key) to validate.");
//
//
//        /**
//         * asking for info to client
//         */
//        subscriptions = new ArrayList<>();
//        while (true) {
//            String user = askInfo("user you want to follow");
//            if (user.equals("")) {
//                break;
//            }
////            Integer resId = Integer.parseInt(res);
//            subscriptions.add(user);
//        }
//        System.out.println(subscriptions.toString());
//
//
//        /**
//         * constructing answer for server
//         *  + processing request server side
//         *  THE REQUEST MUST ENDED WITH "END" WORD
//         */
//        String answer = "FOLLOWER ";
//        for (String user : subscriptions) {
//            answer = answer + user + " ";
//        }
//        answer = answer + "END";
////        System.out.println("answer ===> " + answer);
//
//
//        /**
//         * sending header to server
//         */
//        byte[] message = answer.getBytes();
//        ByteBuffer buffer = ByteBuffer.wrap(message);
//        clientChannel.write(buffer);
//        log("sending: " + answer);
//        buffer.clear();
//
//        // wait for 1 sec before sending next message
//        Thread.sleep(1000);
//
//        for (String id : subscriptions){
//            String idString = String.valueOf(id);
//
//            message = idString.getBytes();
//            buffer = ByteBuffer.wrap(message);
//            clientChannel.write(buffer);
//            log("sending: " + idString);
//            buffer.clear();
//
//            // wait for 1 sec before sending next message
//            Thread.sleep(1000);
//        }
//
//
//        /**
//         * reading answer from server
//         */
//        ByteBuffer bufferReader = ByteBuffer.allocate(256);
//        clientChannel.read(bufferReader);
//        String result = new String(bufferReader.array()).trim();
//        log("--- answer from server : " + result);
//        clientChannel.close();
//
//    }

//    public static void log(String str) {
//        System.out.println(str);
//    }
//
//    public static String askInfo(String info){
//        System.out.print(info + " > ");
//        Scanner sc = new Scanner(System.in);
//        return sc.nextLine();
//    }

}
