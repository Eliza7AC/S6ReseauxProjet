import java.util.ArrayList;

public class Request {

    /**
     * types:
     * sent from clients: PUBLISH, REPLY, REPUBLISH, (UN)SUBSCRIBE
     * sent from server: OK, ERROR
     */

    String type; // PUBLISH
    String user; // @user
    String header; // PUBLISH author:@user
    String body;

    ArrayList<String> infos; // in order to iterate through array when sending infos to the server


    // info - PUBLISH
    public Request(String type, String user, String body) {
        this.type = type;
        this.user = user;
        this.header = type.toUpperCase()+" "+"author:@"+user; // PUBLISH author:@user
        this.body = body;

        this.infos = new ArrayList<>();
        infos.add(type);
        infos.add(user);
        infos.add(body);
    }

    public Request(String msg){
        this.body = msg;
    }

    public String getBody(){
        return this.body;
    }

    public String getHeader() {
        return header;
    }

    public ArrayList<String> getInfos(){
        return infos;
    }
}
