import java.lang.reflect.Array;
import java.util.ArrayList;

public class Subscription {

    private String user; // user followed
    private ArrayList<String> messages;

    public Subscription(String user) {
        this.user = user;
        messages = Database.getMsgFromUser(user);
    }

    public String getUser() {
        return user;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public String toString(){
        return "[" + user + " : " + messages.toString() + "]";
    }

    public void updateMsg(){
        messages = Database.getMsgFromUser(user);
    }


}
