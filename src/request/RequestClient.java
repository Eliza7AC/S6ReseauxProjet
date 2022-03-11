package request;

import java.util.ArrayList;
import java.util.Scanner;

public class RequestClient {
    /**
     * handles different kinds of request
     * info related to request are stored in arraylist
     */
    private ArrayList<String> info; // used to iterate through when sending info to server
    public static final String END = "END"; // idenfity end of the request

    // TODO
    private String header;
    private String body;
    private String type;

    // TODO
//    public static final String END = " $END$"; // idenfity end of the request

    public RequestClient() {
        info = new ArrayList<>();
    }

    protected String askClient(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public ArrayList<String> getInfo() {
        return info;
    }

}
