package DataNetwork;

import java.util.concurrent.ArrayBlockingQueue;

public class Stream {

    private String user;
    private ArrayBlockingQueue<String> stream; // followed users and tags

    public Stream(String user, String userToFollow, String tag) {
        this.user = user;
        stream.add(userToFollow);
        stream.add(tag);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setStream(ArrayBlockingQueue<String> stream) {
        this.stream = stream;
    }

    public void add(String s){
        // s can be either userToFollow or tag
        stream.add(s);
    }

    public String getUser() {
        return user;
    }
}
