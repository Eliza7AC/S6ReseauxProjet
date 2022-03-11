package ClientSide;

import request.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Microblogamu extends Client{

    /**
     * constructor = connection to server
     */
    public Microblogamu() throws IOException {
    }

    public static void main(String[] args) throws IOException, InterruptedException {

//        Client client = new Microblogamu();

        /** connection to server */
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
        log("--- Connecting to ServerSide.Server on port 12345...");


        /**
         * sending to server
         */
        String requestWanted = askInfo("type request");
        RequestClient requestClient = new RequestClient();
        if (requestWanted.equals("PUBLISH")){
            requestClient = new PUBLISH();
        }
        else if (requestWanted.equals("REPLY")){
            requestClient = new REPLY();
        }
        else if (requestWanted.equals("REPUBLISH")){
            requestClient = new REPUBLISH();
        }
        else if (requestWanted.equals("SUBSCRIBE")){
            requestClient = new SUBSCRIBE();
        }
        else if (requestWanted.equals("UNSUBSCRIBE")){
            requestClient = new UNSUBSCRIBE();
        }
        else{
            System.out.println("ERROR");
            System.exit(-1);
        }


        for (String s : requestClient.getInfo()){
            byte[] message = s.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);
            Client.log("sending: " + s);
            buffer.clear();

            // wait for 1 sec before sending next message
            Thread.sleep(1000);
        }

        /**
         * reading from server
         */
        String answer;
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        answer = new String(buffer.array()).trim();
        log("--- answer from server : " + answer);


        /** disconnect */
        clientChannel.close();

    }



}
