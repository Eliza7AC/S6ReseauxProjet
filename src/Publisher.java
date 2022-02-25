import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class Publisher {

    public static void main(String[] args) throws IOException, InterruptedException {

        //  selectable channel for stream-oriented connecting sockets
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 1111));

        log("Connecting to Server on port 1111...");


        ArrayList<Requete> companySample = new ArrayList<>();
        companySample.add(new Requete("Facebook!!!"));
        companySample.add(new Requete("Twitter!!!"));
        companySample.add(new Requete("IBM!!!"));
        companySample.add(new Requete("Google!!!"));
        companySample.add(new Requete("Crunchify!!!"));


        for (Requete r: companySample){
            byte[] message = new String(r.getCorps()).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);


            log("sending: " + r.getCorps());
            buffer.clear();

            // wait for 2 seconds before sending next message
            Thread.sleep(2000);
        }


        // close(): Closes this channel.
        // If the channel has already been closed then this method returns immediately.
        // Otherwise it marks the channel as closed and then invokes the implCloseChannel method in order to complete the close operation.
        clientChannel.close();
    }

    private static void log(String str) {
        System.out.println(str);
    }

}
