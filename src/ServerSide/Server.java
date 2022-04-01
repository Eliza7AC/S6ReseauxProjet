package ServerSide;

import Storage.Database;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import request.RequestServer;

public class Server {

    public static RequestServer requestServer;
    public static int port = 12345;
    // to get several servers running at once
    public static int nbOfServers = 3;

    public static void main(String[] args) throws IOException {
        /** preparing database from PersistenceMsg file */
        Database.getPersistenceData();

        /** initializing server */
        Selector selector = Selector.open(); // selector is open here
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", 12345));
        ssc.configureBlocking(false);
        int ops = ssc.validOps();
        SelectionKey selectKy = ssc.register(selector, ops, null);


        /** creating others servers
         * in order to use several servers at once
         * to get server federation
         */
        SERVEURCONNECT(nbOfServers);




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
                        log("received: " + dataReceived);
                        String[] infoReceivedArr = dataReceived.split(" ");
                        String firstWordReceived = infoReceivedArr[0];

                        if(isHeader(firstWordReceived)){
                            requestServer = new RequestServer(dataReceived); // creation of request header from data of client
                        }

                        /**
                         * THE REQUEST MUST ENDED WITH " END" WORD
                         */
                        if(dataReceived.endsWith(RequestServer.END)){
                            String body = dataReceived;

                            requestServer.setBody(body);
//                            requestServer.update(body); // updating request body with rest of the request

                            /**
                             * answer to client
                             */
                            SocketChannel client = (SocketChannel) myKey.channel();
                            ByteBuffer bufferAnswer = ByteBuffer.wrap(answer.getBytes());
//                            answer = getAnswer();
                            System.out.println(requestServer.getBody());
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


    public static boolean isHeader(String s){
        for(String header: RequestServer.headers){
            if (s.equals(header)){
                return true;
            }
        }
        return false;
    }

    public static void SERVEURCONNECT(int nbOfServers){
//        int nbOfServers = 3;
        for (int i = 0; i < nbOfServers; i++) {

            Thread SubServer = new Thread(new Runnable() {
                public void run() {
                    ServerSocket ServerSocketObject = null;
                    while(true)
                    {
                        try {
                            /** using different ports
                             * one server = one port */
                            port = port+1;
                            ServerSocketChannel sscOther = ServerSocketChannel.open();
                            sscOther.bind(new InetSocketAddress("localhost", port));
                            sscOther.configureBlocking(false);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}
