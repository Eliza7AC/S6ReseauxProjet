package DataNetwork;

public class Message {

    int id;
    String msg;
    String user;
    int replyToId; // msg in response to another
//    ArrayList<Integer> replies; // replies of msg

    public Message(String msg, String user, Integer id) {
        this.id = id;
        this.msg = msg;
        this.user = user;
//        this.replies = new ArrayList<>();
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
        return "{ id: " + id + " | msg: " + msg + " | user: " + user + " | replyToId : " + replyToId  + "}";
    }

    public void addReplies(Message msg){
//        this.replies.add(msg.getId());
    }

    public void addReplyTo(Message msg){
        this.replyToId = msg.getId();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReplyTo(int replyTo) {

        /**
         * updating replies of targeted msg
         */
//        Other.Message targetedMsg = Storage.Database.getMsgFromId(replyToId);
//        targetedMsg.addReplies(this);

        this.replyToId = replyTo;
    }

//    public void setReplies(ArrayList<Integer> replies) {
//        this.replies = replies;
//    }

    public int getReplyToId() {
        return replyToId;
    }

//    public ArrayList<Integer> getReplies() {
//        return replies;
//    }
}
