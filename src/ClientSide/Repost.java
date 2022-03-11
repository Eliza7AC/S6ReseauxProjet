package ClientSide;

import java.io.IOException;
import java.util.ArrayList;

import request.REPUBLISH;
import request.RequestClient;

public class Repost extends Client{

//    public static ArrayList<String> usersFollowed;

    private static ArrayList<String> msgRepublished;


    public Repost() throws IOException {
        /** constructor = connection to server */
        super();
        msgRepublished = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        /** connecting to server by creating new instance */
        Client client = new Repost();
        log("--- Type nothing (just press ENTER key) to validate.");

        /**
         * asking info to user
         */
        String nameClient = askInfo("your username");
        while(true){
            String ans = askInfo("msg id you want to republish");
            if(ans.equals("")){
                break;
            }
            msgRepublished.add(ans);
        }
        System.out.println(msgRepublished.toString());


        /**
         * preparing and sending request, reading data
         */
        for (int i = 0; i < msgRepublished.size(); i++) {
            RequestClient requestClient = new REPUBLISH(nameClient,msgRepublished.get(i));
            client.sendToServer(requestClient);
            client.readingFromServer(client.getClientChannel());
        }

        /**
         * closing connection
         */
        client.disconnectFromServer(client.getClientChannel());

    }



//    public static void main(String[] args) throws IOException {
//        /**
//         * connection to server
//         */
//        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
//        log("--- Connecting to ServerSide.Server on port 12345...");
//
//        /**
//         * asking for info to client
//         */
//        usersFollowed = new ArrayList<>();
//        while(true) {
//            /**
//             * constructing answer for server
//             *  + processing request server side
//             *  THE REQUEST MUST ENDED WITH "END" WORD
//             */
//            String userId = askInfo("id");
//            if (userId.equals("")) {
//                break;
//            }
//
//            String header = "REPUBLISH author:@" + userId + " msg_id:" + userId; // REPUBLISH author:@user msg_id:id END
//            String answer = header + Request.END;
//
//
//            /**
//             * sending header to server
//             */
//            byte[] message = answer.getBytes();
//            ByteBuffer buffer = ByteBuffer.wrap(message);
//            clientChannel.write(buffer);
//            log("sending: " + answer);
//            buffer.clear();
//
//
//            /**
//             * reading answer from server
//             */
//            ByteBuffer bufferReader = ByteBuffer.allocate(256);
//            clientChannel.read(bufferReader);
//            String result = new String(bufferReader.array()).trim();
//            log("--- answer from server : " + result);
//
//        }
//
//        clientChannel.close();
//    }
//
//
//    private static void log(String str) {
//        System.out.println(str);
//    }
//
//    public static String askInfo(String info){
//        System.out.print(info + " > ");
//        Scanner sc = new Scanner(System.in);
//        return sc.nextLine();
//    }

}
