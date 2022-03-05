public class Message {

    int id;
    String msg;
    String user;

    public Message(String msg, String user) {
        this.msg = msg;
        this.user = user;
    }

    public String getMsg(){
        return msg;
    }

    public String user(){
        return user;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return "id: " + id + " | msg: " + msg + " | user: " + user;
    }
}
