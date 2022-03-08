import java.io.*;
import java.util.ArrayList;

public class Database {

    /**
     * messages of database are stored in arraylist
     * by chronological order
     * = older msg at the beginning
     * & most recent msg at the end
     */
    public static ArrayList<Message> storage = new ArrayList<>();



    /**
     * retrieving data from Persistence file
     * only once when server boots (=> and when isConnected is false)
     * and stores data in "storage" arraylist
     */
    public static File file = new File("Persistence");
    public static boolean isConnected = false;

    public static void getPersistenceData() throws IOException {
        if (isConnected == false){
            /**
             * iterating through Persistence file line by line
             * to retrieve data saved
             */
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            int nLine = 0;
            while ((line = br.readLine()) != null)
            {
                // first line is only a comment so we next it
                if (nLine == 0){
                    nLine++;
                    continue;
                }
//                System.out.println(line);
                String[] storageLine = line.split(",");

                // data per line
                int idLine = Integer.parseInt(storageLine[0]);
                String msgLine = storageLine[1];
                String userLine = storageLine[2];

                // adding to storage object
                Message m = new Message(msgLine,userLine,idLine);
                storage.add(m);

//                System.out.println("l'user " + userLine + " a pour id " + idLine + " et son msg est " + msgLine);

            }
            isConnected = true;
        }
    }



    /**
     * when we add new data to arraylist
     * we also add new data to Persistence file
     * => in this way, data is persistent
     */
    public static void addMsg(String msg, String user){
        int id = storage.size(); // final index of the list
        storage.add(new Message(msg,user, id));
        addPersistenceData(msg, user, id);
    }

    public static void addPersistenceData(String msg, String user, int id){
        try{
            FileWriter f = new FileWriter(file.getName(),true);
            BufferedWriter b = new BufferedWriter(f);
            b.write(id+","+msg+","+user);
            b.newLine();
            b.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void showDatabase(){
        for (Message m : storage){
            System.out.println(m.toString());
            System.out.println(" ");
        }
    }


    /** ****** function used in RCV_IDS case ******
     *
     * step 1 = processing by deep copy of the database (Database side)
     * step 2 = then removing all messages which don't match options (Server side)
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
