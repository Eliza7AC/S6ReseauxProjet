import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {

    public static Request request;


    public static void main(String[] args) throws IOException {

        /**
         * preparing database from PersistenceMsg file
         */
        Database.getPersistenceData();


        /**
         * initializing server
         */
        Selector selector = Selector.open(); // selector is open here
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", 12345));
        ssc.configureBlocking(false);
        int ops = ssc.validOps();

        // SelectionKey: A token representing the registration of a SelectableChannel with a Selector.
        // A selection key is created each time a channel is registered with a selector.
        // A key remains valid until it is cancelled by invoking its cancel method, by closing its channel, or by closing its selector.
        SelectionKey selectKy = ssc.register(selector, ops, null);


        String answer = "plop";
        while (true) {

            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iteratorKeys = keys.iterator();

            while (iteratorKeys.hasNext()) {
                SelectionKey myKey = iteratorKeys.next();

                if (myKey.isAcceptable()) {
                    SocketChannel acceptedChannel = ssc.accept();
                    acceptedChannel.configureBlocking(false);
                    acceptedChannel.register(selector, SelectionKey.OP_WRITE);
                    acceptedChannel.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + acceptedChannel.getLocalAddress() + "\n");

                }
                if (myKey.isReadable()) {

                    SocketChannel readableChannel = (SocketChannel) myKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    readableChannel.read(buffer);
                    String dataReceived = new String(buffer.array()).trim();


                    /**
                     * ANALYZING DATA RECEIVED
                     */
                    if(!dataReceived.equals("")){
                        log("Message received: " + dataReceived);
                        String[] infoReceivedArr = dataReceived.split(" ");
                        String firstWordReceived = infoReceivedArr[0];

                        if(isHeader(firstWordReceived)){
                            request = new Request(dataReceived); // creation of request header from data of client
                        }
                        if(isEndOfRequest(dataReceived)){
                            String body = dataReceived;
                            request.update(body); // updating request body with rest of the request

                            /**
                             * answer to client
                             */
                            SocketChannel client = (SocketChannel) myKey.channel();
                            ByteBuffer bufferAnswer = ByteBuffer.wrap(answer.getBytes());
                            answer = getAnswer();
                            bufferAnswer = ByteBuffer.wrap(answer.getBytes());
                            client.write(bufferAnswer);
                            log("sending answer: " + answer);


                            /**
                             * clearing all previous infos
                             */
                            bufferAnswer.clear();
                        }
                    }

                    /**
                     * closing connexion
                     */
                    if (dataReceived.equals("EXIT")) {
                        log("\ntime to close connection");
                        readableChannel.close();
                    }
                }
//                if (myKey.isWritable()){
//                    SocketChannel client = (SocketChannel) myKey.channel();
////                    ByteBuffer buffer = (ByteBuffer) myKey.attachment();
//
//                    ByteBuffer bufferAnswer = ByteBuffer.wrap("OK".getBytes());
//                    client.write(bufferAnswer);
//                    log("sending: " + "OK");
//                    bufferAnswer.clear();
//
//                }

                iteratorKeys.remove();
            }


        }
    }



    /**
     * ************************************
     * *********** methods ****************
     * ************************************
     */


    private static void log(String str) {
        System.out.println(str);
    }



    public static boolean isHeader(String s){
        for(String header: Request.headers){
            if (s.equals(header)){
                return true;
            }
        }
        return false;
    }

    public static boolean isEndOfRequest(String s){
        return s.endsWith("END");
    }


    /**
     * preparing answer according to request received
     */
    public static String getAnswer(){
        String answer = "";

        /**
         * justing printing a few infos about request
         */
        System.out.println(request.getType() + "........................");
        System.out.println("HEADER = " + request.getHeader());
        System.out.println("BODY = " + request.getBody());

        if(request.getType().equals("PUBLISH")){
            Database.addMsg(request.getBody(), request.getUser());
            System.out.println("database: " + Database.storageMsg.toString());
            answer = "OK";
        }
        else if(request.getType().equals("RCV_IDS")){

            /**
             * processing by deep copy of the database in new ArrayList
             * then removing all messages which don't match the options
             */
            ArrayList<Message> results = Database.deepCopy();

            System.out.println(request.getUser());
            if (request.getUser()!=null){
                System.out.println("if !user.isEmpty() " + request.getUser());
                Database.getMsgFromUser(request.getUser(),results);
                System.out.println(results.toString());
            }
            if (!request.getOptionTag().isEmpty()){
                System.out.println("if !tag.isEmpty() " + request.getOptionTag());
                Database.getMsgFromTag(request.getOptionTag(),results);
                System.out.println(results.toString());
            }
            if(!request.getOptionId().isEmpty()){
                System.out.println(" if !id.isEmpty() " + request.getOptionId());
                int idInt = Integer.parseInt(request.getOptionId());
                System.out.println(idInt+2);
                Database.getMsgSinceId(idInt, results);
                System.out.println(results.toString());
            }
            if(!request.getOptionLimit().isEmpty()){
                System.out.println(" if !limit.isEmpty() " + request.getOptionLimit());
                Database.getMsgWithLimit(request.getOptionLimit(), results);
                System.out.println(results.toString());
            }
            // construction of answer
            answer = "RCV_IDS ";
            for (Message m : results){
                answer = answer + " | " + m.getId();
            }
            results.clear();

        }
        else if(request.getType().equals("RCV_MSG")){
            String idString = request.getOptionId().substring(0,1);
            System.out.println(idString);
            Integer idInt = Integer.parseInt(idString);
            Message msg = Database.getMsgFromId(idInt);
            answer = "MSG " + msg.getMsg();
        }
        else if (request.getType().equals("FOLLOWER")){
            String[] followerRequest = request.getBody().split(" ");
//            ArrayList<String> subscriptionUsers = new ArrayList<>();
            boolean oneOfUsersExists = false;

            // example of followerRequest: FOLLOWER adeline Descartes kevin END
            // => so we exclude first and last word
            for (int i = 1; i < followerRequest.length; i++) {

                // adding subscription to database
                String userName = followerRequest[i];
                if (Database.userExists(userName)){
                    Subscription subscription = new Subscription(userName); // retrieving msg from user
                    Database.addSubscription(subscription);

                    // preparing answer from arraylist
                    ArrayList<String> msgFromUser = new ArrayList<>();
                    msgFromUser.add(userName); // adding name of user
                    msgFromUser.addAll(subscription.getMessages()); // adding msg of user
                    answer = answer+msgFromUser.toString();

                    oneOfUsersExists = true;
                }

            }
            if (!oneOfUsersExists){
                answer = "ERROR : THIS USER DOESN'T EXIST";
            }
//            Database.showSubscriptionDatabase();
        }
        else if(request.getType().equals("REPLY")){
            String[] replyData = request.getHeader().split(" "); // ex: REPLY author:@me reply_to_id:id2END
            String msgAuthor = replyData[1].split("author:@")[1];
            String replyId = replyData[2].split("id:")[1];
            int replyIdint = Integer.parseInt(replyId);
            String msgContent = request.getBody();

            if (Database.msgExists(replyIdint)){
                Message message = new Message(msgContent,msgAuthor,Database.storageMsg.size());
                message.setReplyTo(replyIdint);
                Database.addMsg(message);

                System.out.println("replyToId = " + message.getReplyToId());
                Database.showMsgDatabase();
                answer = "OK";
            }
            else{
                answer = "ERROR";
            }

        }
        return answer;
    }

}
