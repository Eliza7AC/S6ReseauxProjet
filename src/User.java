import java.util.ArrayList;

public class User {

    private String name;
    private ArrayList<String> followers;
    private ArrayList<Message> messages;

    public User(String name) {
        this.name = name;
        this.followers = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void addFollowers(String follower) {
        this.followers.add(follower);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessages(Message msg) {
        this.messages.add(msg);
    }
}
