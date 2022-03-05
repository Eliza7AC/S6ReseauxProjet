import java.util.ArrayList;

public class Database {


    /**
     * stored by chronological order
     * starts with the older msg
     * ends with the most recent msg
     */
    public static ArrayList<Message> storage = new ArrayList<>();


    public static void addMsg(String msg, String user){
        int id = storage.size(); // final index of the list
        Message message = new Message(msg,user, id);
        storage.add(message);
    }

    public static void showDatabase(){
        for (Message m : storage){
            System.out.println(m.toString());
        }
    }


//    public static void addMsg(Message msg){
//        storage.add(msg);
//    }

    public static void getMsgFromUser(String user, ArrayList<Message> listOfMsg){
        for(Message msg: storage){
            if (user.equals(msg.getUser())){
                listOfMsg.add(msg);
            }
        }

    }


    public static void getMsgSinceId(int id, ArrayList<Message> listOfMsg){
        for(Message msg: storage){
            if (msg.getId() > id){
                listOfMsg.add(msg);
            }
        }

    }

    public static Message getMsgFromId(int id){

        for (Message m : storage){
            if (m.getId() == id){
                return m;
            }
        }
        return null;
    }


    // INFO - author:@user l’auteur des messages est @user
//    public ArrayList<String> getMsgFromUser(String user){
//        ArrayList<String> msgFromUser = new ArrayList<>();
//
//        // scanning and retrieving msg from user
//        for (int i = 0; i < dataBase.size(); i++) {
//            String actualUser = dataBase.get(i).get(0);
//            if (actualUser.equals(user)){
//                msgFromUser.add(dataBase.get(i).get(1));
//            }
//        }
//
//        return msgFromUser;
//    }
//
//
//    // INFO - tag:#tag les messages contiennent le mot clé #tag
//    public ArrayList<String> getMsgFromTag(String tag){
//        ArrayList<String> msgWithTag = new ArrayList<>();
//
//        for (int i = 0; i < dataBase.size(); i++) {
//            String msgScanned = dataBase.get(i).get(1);
//            if (msgScanned.contains(tag)){
//                msgWithTag.add(msgScanned);
//            }
//        }
//        return msgWithTag;
//    }
//
//    // INFO - since_id:id les messages ont été publiés après le message dont l’identifiant est id
//    public ArrayList<String> getMsgAfter(int id){
//        ArrayList<String> msgAfter = new ArrayList();
//
//        for (int i = id; i < dataBase.size(); i++) {
//            String actualMsg = dataBase.get(i).get(1);
//            msgAfter.add(actualMsg);
//        }
//        return msgAfter;
//    }

    // INFO - fds


}
