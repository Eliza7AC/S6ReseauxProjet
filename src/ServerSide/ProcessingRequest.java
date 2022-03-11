package ServerSide;

import Storage.Database;
import DataNetwork.*;
import request.RequestServer;

import java.util.ArrayList;

public class ProcessingRequest {

    /**
     * this class is aimed to process request received by server
     * according to request type (PUBLISH, RCV_MSG, REPUBLISH, etc.)
     * in order to prepare an answer and send it to the client
     */
    public static String getAnswer(RequestServer requestServer){

        String answer = "";

        /**
         * justing printing a few infos about request
         */
        System.out.println(requestServer.getType() + "........................");
        System.out.println("HEADER ----- " + requestServer.getHeader());
        System.out.println("BODY ------ " + requestServer.getBody());


        /**
         * analysing request type
         */
        if(requestServer.getType().equals("PUBLISH")){
            if(requestServer.getBody().length() <= 256){
                Database.addMsg(requestServer.getBody(), requestServer.getUser());
//                System.out.println("database: " + Storage.Database.storageMsg.toString());
                answer = "OK";
            }
            else{
                answer = "ERROR : MESSAGE LENGTH";
            }
        }
        else if(requestServer.getType().equals("RCV_IDS")){
            /**
             * processing by deep copy of the database in new ArrayList
             * then removing all messages which don't match the options
             * to retrieve only targeted msg
             */
            ArrayList<Message> results = Database.deepCopy();

            System.out.println(requestServer.getUser());
            if (requestServer.getUser()!=null){
                Database.getMsgFromUser(requestServer.getUser(),results);
                System.out.println(results.toString());
            }
            if (!requestServer.getOptionTag().isEmpty()){
                Database.getMsgFromTag(requestServer.getOptionTag(),results);
                System.out.println(results.toString());
            }
            if(!requestServer.getOptionId().isEmpty()){
                int idInt = Integer.parseInt(requestServer.getOptionId());
                System.out.println(idInt+2);
                Database.getMsgSinceId(idInt, results);
                System.out.println(results.toString());
            }
            if(!requestServer.getOptionLimit().isEmpty()){
                Database.getMsgWithLimit(requestServer.getOptionLimit(), results);
                System.out.println(results.toString());
            }

            // final step in construction of answer
            answer = "RCV_IDS";
            if(!results.isEmpty()){
                for (Message m : results){
                    answer = answer + " | " + m.getId();
                }
            }
            else{
                answer = "ERROR : NO MESSAGE MATCHING YOUR SEARCH";
            }
            results.clear();

        }
        else if(requestServer.getType().equals("RCV_MSG")){
            System.out.println("getOptionId " + requestServer.getOptionId());




            Integer idInt = Integer.parseInt(requestServer.getOptionId());
            if (Database.msgExists(idInt)){
                System.out.println("l'id est " + idInt);
                Message msg = Database.getMsgFromId(idInt);
                answer = "MSG " + msg.getMsg();
            }
            else{
                answer = "ERROR : NO MESSAGE MATCHING THIS ID";
            }

        }
        else if (requestServer.getType().equals("FOLLOWER")){
            String[] followerRequest = requestServer.getBody().split(" ");
            ArrayList<String> subscriptionUsers = new ArrayList<>();
            boolean oneOfUsersExists = false;

            // example of followerRequest: FOLLOWER adeline Descartes kevin END
            // => so we exclude first and last word
            for (int i = 1; i < followerRequest.length; i++) {

                // adding subscription to database
                String userName = followerRequest[i];
                if (Database.userExists(userName)){
                    Subscription subscription = new Subscription(userName); // retrieving msg from user
                    Database.addSubscription(subscription);

                    // preparing answer from arraylist
                    ArrayList<String> msgFromUser = new ArrayList<>();
                    msgFromUser.add(userName); // adding name of user
                    msgFromUser.addAll(subscription.getMessages()); // adding msg of user
                    answer = answer+msgFromUser.toString();

                    oneOfUsersExists = true;
                }

            }
            if (!oneOfUsersExists){
                answer = "ERROR : THIS USER DOESN'T EXIST";
            }
            Storage.Database.showSubscriptionDatabase();
        }
        else if(requestServer.getType().equals("REPLY")){
            String[] replyData = requestServer.getHeader().split(" "); // ex: REPLY author:@me reply_to_id:id2END
            String msgAuthor = replyData[1].split("author:@")[1];
            String replyId = replyData[2].split("id:")[1];
            int replyIdint = Integer.parseInt(replyId);
            String msgContent = requestServer.getBody();

            if (Database.msgExists(replyIdint)){
                Message message = new Message(msgContent,msgAuthor,Database.storageMsg.size());
                message.setReplyTo(replyIdint);
                Database.addMsg(message);

                System.out.println("replyToId = " + message.getReplyToId());
                Database.showMsgDatabase();
                answer = "OK";
            }
            else{
                if(Database.msgExists(replyIdint) || requestServer.getBody().length()>256){
                    answer = "ERROR";
                }
            }

        }
        else if(requestServer.getType().equals("REPUBLISH")){
            String[] dataReceived = requestServer.getHeader().split(" "); // REPUBLISH author:@user msg_id:id END
            Integer id = Integer.parseInt(dataReceived[2].split("msg_id:")[1]);

            if(Database.msgExists(id)){
                /**
                 * new publish from id
                 */
                Message msgRetrieved = Database.getMsgFromId(id);

                String user = dataReceived[1].split("author:@")[1];
                Database.addMsg(msgRetrieved.getMsg(),user);
                answer = "OK : " + Database.getMsgFromId(id).getMsg();
            }
            else{
                answer = "ERROR : NO MESSAGE MATCHING THIS ID";
            }
        }
        else if (requestServer.getType().equals("CONNECT")){
            String[] dataReceived = requestServer.getHeader().split(" "); // CONNECT user:@user END
            String user = dataReceived[1].split("user:@")[1];
            System.out.println(user);

            // first adding of user to stream list
            Stream stream = new Stream(user,null,null);
            Database.addSubscriptionStream(stream);

            if (user.equals("")){
                answer = "ERROR";
            }
            else{
                answer = "OK";
            }
        }
        else if (requestServer.getType().equals("SUBSCRIBE")){ // SUBSCRIBE author:@user et SUBSCRIBE tag:#tag
            String[] dataReceived = requestServer.getHeader().split(" ");
            String user = dataReceived[1].split("user:@")[1];
            String tag = dataReceived[1].split("tag:#")[1];

            try{
                Stream stream = Database.getStream();
                stream.add(user);
                stream.add(tag);
                answer = "OK";
            }
            catch(Exception e){
                answer = "ERROR";
            }
        }
        else if (requestServer.getType().equals("UNSUBSCRIBE")){ // UNSUBSCRIBE author:@user et UNSUBSCRIBE tag:#tag
            String[] dataReceived = requestServer.getHeader().split(" ");
            String user = dataReceived[1].split("user:@")[1];
            String tag = dataReceived[1].split("tag:#")[1];

            try{
                Stream stream = Database.getStream();
                Database.getFullStream().remove(stream);
                stream.add(tag);
                answer = "OK";
            }
            catch(Exception e){
                answer = "ERROR";
            }

        }
        return answer;
    }

}
