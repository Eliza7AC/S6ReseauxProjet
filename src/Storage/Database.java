package Storage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import DataNetwork.*;

public class Database {


    public static ArrayList<Stream> listOfSubscriptions = new ArrayList<>();

    /**
     * messages of database are stored in arraylist
     * by chronological order
     * = older msg at the beginning
     * & most recent msg at the end
     */
    public static File file = new File("Storage/PersistenceMsg");
    public static ArrayList<Message> storageMsg = new ArrayList<>();

    /**
     * subscriptions of database are stored in arraylist
     */
    public static File fileSubscription = new File("Storage/PersistenceSubscription");
    public static ArrayList<Subscription> storageSubscription = new ArrayList<>();



    /**
     * retrieving data from PersistenceMsg and PersistenceSubscription files
     * only once when server boots
     * and stores data in both matching arraylist
     */
    public static void getPersistenceData() throws IOException {
        /**
         * iterating through PersistenceMsg file line by line
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
//            System.out.println(m.getReplyToId());

            String replyIdLine = "";
            if (storageLine.length>3){ // if there is an idToReply
                replyIdLine = storageLine[3]; // idToReply = 4th element
            }
            else{
                replyIdLine = "-1";
            }
            m.setReplyTo(Integer.parseInt(replyIdLine));
            storageMsg.add(m);

//                System.out.println("l'user " + userLine + " a pour id " + idLine + " et son msg est " + msgLine);

        }
        showMsgDatabase();


        /**
         * iterating through PersistenceSubscription file line by line
         * to retrieve data saved
         */
        BufferedReader br2 = new BufferedReader(new FileReader(fileSubscription));
        String line2 = null;
        int nLine2 = 0;
        while ((line2 = br2.readLine()) != null)
        {
            // first line is only a comment so we next it
            if (nLine == 0){
                nLine++;
                continue;
            }
//                System.out.println(line);
            String[] storageLine = line2.split(",");

            // data per line
//            String userLine = "";
//            ArrayList<String> msgLine = new ArrayList<>();
//            for (int i = 0; i < storageLine.length; i++) {
//                if (i==0){ // if info is username
//                    userLine = storageLine[i];
//                }
//                else{
//                    msgLine.add(storageLine[i]);
//                }
//            }

            // new DataNetwork.Subscription object + adding to subscription storage
            Subscription subscription = new Subscription(line2);
            storageSubscription.add(subscription);
        }
//        showSubscriptionDatabase();


    }



    /**
     * when we add new data to arraylist
     * we also add new data to PersistenceMsg file
     * => in this way, data is persistent
     */
    public static void addMsg(String msg, String user){
        int id = storageMsg.size(); // final index of the list
        Message newMsg = new Message(msg, user, id);
        newMsg.setReplyTo(-1);
        storageMsg.add(newMsg);
        addPersistenceMsg(msg, user, id, newMsg.getReplyToId());

        if (userIsFollowed(user)!=null){
            // updating msg of followed user after adding new msg to the database
            userIsFollowed(user).updateMsg();
        }
    }


    public static void addMsg(Message msg){
//        int id = storageMsg.size(); // final index of the list
//        msg.setId(id);

        storageMsg.add(msg);
        addPersistenceMsg(msg.getMsg(), msg.getUser(), msg.getId(),msg.getReplyToId());

        if (userIsFollowed(msg.getUser())!=null){
            // updating msg of followed user after adding new msg to the database
            userIsFollowed(msg.getUser()).updateMsg();
        }
    }

    private static void addPersistenceMsg(String msg, String user, int id, Integer idToReply){
        try{
            FileWriter f = new FileWriter(file.getPath(),true);
            BufferedWriter b = new BufferedWriter(f);

            if(idToReply != -1){
                b.write(id+","+msg+","+user+","+idToReply);
            }
            else{
                b.write(id+","+msg+","+user+",-1");
            }
            b.newLine();
            b.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * adding subscription to arraylist of database
     * AND adding info to persistent file
     * = data persistent
     */
    public static void addSubscription(Subscription s){
        storageSubscription.add(s);
        addPersistenceSubscription(s.getUser());
    }

    private static void addPersistenceSubscription(String user){
        try{
            FileWriter f2 = new FileWriter(fileSubscription.getPath(),true);
            BufferedWriter b2 = new BufferedWriter(f2);
            b2.write(user);
            b2.newLine();
            b2.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void showMsgDatabase(){
        for (Message m : storageMsg){
            System.out.println(m.toString());
        }
    }

    public static void showSubscriptionDatabase(){
        for (Subscription s : storageSubscription){
            System.out.println(s.toString());
            System.out.println(" ");
        }
    }


    /** ****** function used in RCV_IDS case ******
     *
     * step 1 = processing by deep copy of the database (Storage.Database side)
     * step 2 = then removing all messages which don't match options (ServerSide.Server side)
     */
    public static ArrayList<Message> deepCopy(){
        ArrayList<Message> newList = new ArrayList<>();
        for(Message m : storageMsg){
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


    // INFO - msg published after this id
    public static void getMsgSinceId(int id, ArrayList<Message> results){
        results.removeIf(msg -> msg.getId()<=id);
    }


    // INFO - msg with this specified id
    public static Message getMsgFromId(int id){
        for (Message m: storageMsg){
            if (m.getId() == id){
                return m;
            }
        }
        return null;
    }


    // INFO - get the most recent n msg
    public static void getMsgWithLimit(String limit, ArrayList<Message> results){
        int limitInt = Integer.parseInt(limit);

        if (results.size() < limitInt){
            return;
        }
        while (results.size()>limitInt){
            results.remove(0);
        }
    }


    // INFO - author:@user l’auteur des messages est @user
    public static ArrayList<String> getMsgFromUser(String user){
        ArrayList<String> msgFromUser = new ArrayList<>();

        for (Message m: storageMsg){
            if (m.getUser().equals(user)){
                msgFromUser.add(m.getMsg());
            }
        }

        return msgFromUser;
    }

    public static Subscription userIsFollowed(String user){
        for(Subscription s : storageSubscription){
            if (s.getUser().equals(user)){
                return s;
            }
        }
        return null;
    }

    public static boolean userExists(String user){
        for (Message m : storageMsg){
            if (m.getUser().equals(user)){
                return true;
            }
        }
        return false;
    }

    public static boolean msgExists(int id){
        for (Message m : storageMsg){
            if (m.getId() == id){
                return true;
            }
        }
        return false;
    }

    public static void addSubscriptionStream(Stream stream){
        listOfSubscriptions.add(stream);
    }

    public static Stream getStream(){
        return listOfSubscriptions.get(0);
    }

    public static ArrayList<Stream> getFullStream(){
        return listOfSubscriptions;
    }


}
