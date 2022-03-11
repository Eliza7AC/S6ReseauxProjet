package ServerSide;

import DataNetwork.Stream;
import Storage.Database;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import request.RequestServer;

public class Microblogamu_central {

    public static RequestServer requestServer;

//    ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<T>();



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
                        log("Other.Message received: " + dataReceived);
                        String[] infoReceivedArr = dataReceived.split(" ");
                        String firstWordReceived = infoReceivedArr[0];

                        if(Server.isHeader(firstWordReceived)){
                            requestServer = new RequestServer(dataReceived); // creation of request header from data of client
                        }

                        /**
                         * THE REQUEST MUST ENDED WITH " END" WORD
                         */
                        if(dataReceived.endsWith(RequestServer.END)){
                            String body = dataReceived;
                            System.out.println(body);
                            requestServer.update(body); // updating request body with rest of the request

                            // INFO - processing and handling data
                            String user = dataReceived.split(" ")[1];
                            if (Database.listOfSubscriptions.contains(user)){
                                for(Stream s : Database.listOfSubscriptions){
                                    if (s.getUser().equals(user)){
                                        if(dataReceived.split(" ")[0].startsWith("SUBSCRIBE")){
                                            Database.addSubscriptionStream(new Stream(null,null,null));
                                        }
                                        else if(dataReceived.split(" ")[0].startsWith("UNSUBSCRIBE")){
//                                            Database.addSubscriptionStream(new Stream(s,null,null));
                                        }
                                    }
                                }
                            }

                            /**
                             * answer to client
                             */
                            SocketChannel client = (SocketChannel) myKey.channel();
                            ByteBuffer bufferAnswer = ByteBuffer.wrap(answer.getBytes());
//                            answer = getAnswer();
                            answer = ProcessingRequest.getAnswer(requestServer);
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

    private static void log(String str) {
        System.out.println(str);
    }

}
