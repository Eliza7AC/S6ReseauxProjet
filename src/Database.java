import java.util.ArrayList;

public class Database {


    /**
     * messages stored by chronological order
     * starts with the older msg
     * ends with the most recent msg
     */
    public static ArrayList<Message> storage = new ArrayList<>();

//    public static ArrayList<User> subscriptions = new ArrayList<>();


    public static void addMsg(String msg, String user){
        int id = storage.size(); // final index of the list
        storage.add(new Message(msg,user, id));
    }

    public static void showDatabase(){
        for (Message m : storage){
            System.out.println(m.toString());
            System.out.println(" ");
        }
    }



    /**
     * processing by deep copy of the database
     * then removing all messages that doesn't match to options
     */
    public static ArrayList<Message> deepCopy(){
        ArrayList<Message> newList = new ArrayList<>();
        for(Message m : storage){
            newList.add(m);
        }
        return newList;
    }

    public static void getMsgFromUser(String user, ArrayList<Message> results){
        results.removeIf(msg -> !msg.getUser().equals(user));
    }


    public static void getMsgFromTag(String tag, ArrayList<Message> results){
        results.removeIf(msg -> !msg.getMsg().contains(tag));
    }

    public static void getMsgSinceId(int id, ArrayList<Message> results){
        results.removeIf(msg -> msg.getId()<=id);
    }

    public static void getMsgWithLimit(String limit, ArrayList<Message> results){
        int limitInt = Integer.parseInt(limit);

        if (results.size() < limitInt){
            return;
        }
        while (results.size()>limitInt){
            results.remove(0);
        }
    }


    public static Message getMsgFromId(int id){
        for (Message m: storage){
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


}
