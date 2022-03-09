import java.util.ArrayList;

public class Message {

    int id;
    String msg;
    String user;

    // TODO
    ArrayList<Message> replies;

    public Message(String msg, String user, int id) {
        this.id = id;
        this.msg = msg;
        this.user = user;
        this.replies = new ArrayList<>();
    }

    public String getMsg(){
        return msg;
    }

    public String getUser(){
        return this.user;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return "{ id: " + id + " | msg: " + msg + " | user: " + user + "}";
    }

    public void addReply(Message msg){
        this.replies.add(msg);
    }
}
