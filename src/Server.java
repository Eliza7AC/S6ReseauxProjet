import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static boolean requestReceived = false;

    public static ArrayList<String> headers;

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open(); // selector is open here
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", 12345));
        ssc.configureBlocking(false);

        int ops = ssc.validOps();

        // SelectionKey: A token representing the registration of a SelectableChannel with a Selector.
        // A selection key is created each time a channel is registered with a selector.
        // A key remains valid until it is cancelled by invoking its cancel method, by closing its channel, or by closing its selector.
        SelectionKey selectKy = ssc.register(selector, ops, null);



        headers = new ArrayList<>();
        headers.add("PUBLISH");
        headers.add("RCV_IDS");
        headers.add("RCV_MSG");
        headers.add("REPLY");
        headers.add("REPUBLISH");

        /**
         * saving info received from client
         */
        String user = "";
        String type = "";
        String body = "";
        String tag = "";
        String id = "";
        String limit = "";

        /**
         * answer from the server to the client
         */
        String answer = "plop";



        while (true) {
//            log("I'm a server and I'm waiting for new connection and buffer select...");

            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iteratorKeys = keys.iterator();

            while (iteratorKeys.hasNext()) {
                SelectionKey myKey = iteratorKeys.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    SocketChannel acceptedChannel = ssc.accept();

                    // Adjusts this channel's blocking mode to false
                    acceptedChannel.configureBlocking(false);

                    // Operation-set bit for read operations
                    acceptedChannel.register(selector, SelectionKey.OP_WRITE);
                    acceptedChannel.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + acceptedChannel.getLocalAddress() + "\n");


                    // Tests whether this key's channel is ready for reading
                }
                if (myKey.isReadable()) {

                    SocketChannel readableChannel = (SocketChannel) myKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    readableChannel.read(buffer);
                    String result = new String(buffer.array()).trim();


                    /**
                     * *********** READING REQUEST
                     * *********** ANALYSING METADATA
                     */
                    if(!result.equals("")){
                        log("Message received: " + result);
//                        String infoReceivedS = result;
                        String[] infoReceivedArr = result.split(" ");
                        String firstWord = infoReceivedArr[0];

                        /**
                         * *********** READING HEADER
                         */
                        if(isHeader(firstWord)){
                            type = firstWord;

                            /**
                             * identifying author
                             */
                            if(requestContains("author:@", infoReceivedArr)){
                                user = getInfoFromRequest("@", infoReceivedArr); // author:@aline
                                String[] userString = user.split("@"); // author: aline
                                user = userString[1]; // aline
                                System.out.println("user ==== " + user);
                            }
                            /**
                             * identifying tag
                             */
                            if(requestContains("tag:#", infoReceivedArr)){
                                tag = getInfoFromRequest("#", infoReceivedArr); // tag:#bonjour
                                String[] tagString = tag.split("#"); // tag: bonjour
                                tag = tagString[1]; // bonjour
                                System.out.println("tag === " + tag);
                            }
                            /**
                             * identifying id
                             */
//                            System.out.println(requestContains("since_id:",infoReceivedArr));
                            if(requestContains("since_id:",infoReceivedArr)){
                                id = getInfoFromRequest("since_id", infoReceivedArr); // since_id:5
                                String[] idString = id.split("since_id:"); // 5
                                id = idString[1];
                                System.out.println("id === " + id);
                            }
                            if(requestContains("msg_id:",infoReceivedArr)){
                                id = getInfoFromRequest("msg_id", infoReceivedArr); // msg_id:5
                                String[] idString = id.split("msg_id:"); // 5
                                id = idString[1];
                                System.out.println("id === " + id);
                            }

                            /**
                             * identifying limit
                             */
                            if(requestContains("limit:",infoReceivedArr)){
                                limit = getInfoFromRequest("limit:", infoReceivedArr); // limit:10
                                String[] limitString = limit.split("limit:"); // 10
                                limit = limitString[1];
                                System.out.println("limit === " + limit);
                            }

                        }



                        /**
                         * *********** CHECKING IF END OF THE REQUEST
                         * *********** THEN PROCESSING THE REQUEST
                         */
                        if (result.endsWith("END")){
                            requestReceived = false; // to start over with next request
                            SocketChannel client = (SocketChannel) myKey.channel();

                            /**
                             * answer to client
                             */
                            ByteBuffer bufferAnswer = ByteBuffer.wrap(answer.getBytes());

                            if(type.equals("PUBLISH")){
                                body = result.substring(0,result.length()-3);
                                Database.addMsg(body,user);
                                System.out.println("database: " + Database.storage.toString());
                                answer = "OK";
                            }
                            else if(type.equals("RCV_IDS")){

                                /** INFO
                                 * processing by deep copy of the database in new ArrayList
                                 * then removing all messages that doesn't match the options
                                 */
                                ArrayList<Message> results = Database.deepCopy();

                                if (!user.isEmpty()){
                                    System.out.println("if !user.isEmpty() " + user);
                                    Database.getMsgFromUser(user,results);
                                    System.out.println(results.toString());
                                }
                                if (!tag.isEmpty()){
                                    System.out.println("if !tag.isEmpty() " + tag);
                                    Database.getMsgFromTag(tag,results);
                                    System.out.println(results.toString());
                                }
                                if(!id.isEmpty()){
                                    System.out.println(" if !id.isEmpty() " + id);
                                    int idInt = Integer.parseInt(id);
                                    System.out.println(idInt+2);
                                    Database.getMsgSinceId(idInt, results);
                                    System.out.println(results.toString());
                                }
                                if(!limit.isEmpty()){
                                    System.out.println(" if !limit.isEmpty() " + limit);
                                    Database.getMsgWithLimit(limit, results);
                                    System.out.println(results.toString());
                                }

                                /**
                                 * id results accroding to research
                                 */
                                answer = "RCV_IDS ";
                                for (Message m : results){
                                    answer = answer + " | " + m.getId();
                                }
                                results.clear();


                            }
                            // TODO - doesn't work
                            else if(type.equals("RCV_MSG")){
                                Integer idInt = Integer.parseInt(id);
                                Message msg = Database.getMsgFromId(idInt);
                                answer = "MSG " + msg.getMsg();
                            }

                            bufferAnswer = ByteBuffer.wrap(answer.getBytes());
                            client.write(bufferAnswer);
                            log("sending answer: " + answer);


                            /**
                             * clearing all previous infos
                             */
                            bufferAnswer.clear();
                            user = "";
                            type = "";
                            body = "";
                            tag = "";
                            id = "";
                            limit = "";


                        }

                    }


                    /**
                     * closing connexion
                     */
                    if (result.equals("EXIT")) {
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

    private static void log(String str) {
        System.out.println(str);
    }

    public static boolean isUpperCase(String s)
    {
        for (int i=0; i<s.length(); i++)
        {
            if (!Character.isUpperCase(s.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }


    public static boolean requestContains(String pattern, String[] requestReceived){
        for(String s: requestReceived){
            if (s.contains(pattern)){
                return true;
            }
        }
        return false;
    }

    public static String getInfoFromRequest(String pattern, String[] requestReceived){
        for(String s: requestReceived){
            if (s.contains(pattern)){
                return s;
            }
        }
        return null;
    }


    public static boolean isHeader(String s){
        for(String header: headers){
            if (s.equals(header)){
                return true;
            }
        }
        return false;
    }


}
