package ClientSide;

import java.io.IOException;

import request.PUBLISH;
import request.RequestClient;

public class Publisher extends Client {

    public Publisher() throws IOException {
        /** super() constructor retrieves methods related to connection to server,
         * response from server and closing connection */
        super();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        /** connecting to server by creating new instance */
        Client client = new Publisher();


        /** preparing request */
        RequestClient requestClient = new PUBLISH();
//        RequestNew requestNew = new REPLY();

        /** sending request */
        client.sendToServer(requestClient);

        /** reading from server and disconnect */
        client.readingFromServer(client.getClientChannel());
        client.disconnectFromServer(client.getClientChannel());

    }


//    public static void main(String[] args) throws IOException, InterruptedException {
//
//
//        /**
//         * connection to server
//         * with selectable channel for stream-oriented connecting sockets
//         */
//        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
//        log("--- Connecting to ServerSide.Server on port 12345...");
//
//
//        /**
//         * sending request data to server
//         */
//        Request request = new Request();
//
//
//        for (String s : request.getData()){
//            byte[] message = s.getBytes();
//            ByteBuffer buffer = ByteBuffer.wrap(message);
//            clientChannel.write(buffer);
//            log("sending: " + s);
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
//        ByteBuffer buffer = ByteBuffer.allocate(256);
//        clientChannel.read(buffer);
//        String result = new String(buffer.array()).trim();
//        log("--- answer from server : " + result);
//        clientChannel.close();
//
//    }
//
//
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
