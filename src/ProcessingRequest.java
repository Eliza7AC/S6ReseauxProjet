import java.util.ArrayList;

public class ProcessingRequest {

    /**
     * this class is aimed to process request received by server
     * according to request type (PUBLISH, RCV_MSG, REPUBLISH, etc.)
     * in order to prepare an answer and send it to the client
     */
    public static String getAnswer(Request request){
        String answer = "";

        /**
         * justing printing a few infos about request
         */
        System.out.println(request.getType() + "........................");
        System.out.println("HEADER ----- " + request.getHeader());
        System.out.println("BODY ------ " + request.getBody());


        /**
         * analysing request type
         */
        if(request.getType().equals("PUBLISH")){
            if(request.getBody().length() <= 256){
                Database.addMsg(request.getBody(), request.getUser());
//                System.out.println("database: " + Database.storageMsg.toString());
                answer = "OK";
            }
            else{
                answer = "ERROR : MESSAGE LENGTH";
            }
        }
        else if(request.getType().equals("RCV_IDS")){
            /**
             * processing by deep copy of the database in new ArrayList
             * then removing all messages which don't match the options
             * to retrieve only targeted msg
             */
            ArrayList<Message> results = Database.deepCopy();

            System.out.println(request.getUser());
            if (request.getUser()!=null){
                Database.getMsgFromUser(request.getUser(),results);
                System.out.println(results.toString());
            }
            if (!request.getOptionTag().isEmpty()){
                Database.getMsgFromTag(request.getOptionTag(),results);
                System.out.println(results.toString());
            }
            if(!request.getOptionId().isEmpty()){
                int idInt = Integer.parseInt(request.getOptionId());
                System.out.println(idInt+2);
                Database.getMsgSinceId(idInt, results);
                System.out.println(results.toString());
            }
            if(!request.getOptionLimit().isEmpty()){
                Database.getMsgWithLimit(request.getOptionLimit(), results);
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
        else if(request.getType().equals("RCV_MSG")){
            System.out.println("getOptionId " + request.getOptionId());
            Integer idInt = Integer.parseInt(request.getOptionId());
            if (Database.msgExists(idInt)){
                System.out.println("l'id est " + idInt);
                Message msg = Database.getMsgFromId(idInt);
                answer = "MSG " + msg.getMsg();
            }
            else{
                answer = "ERROR : NO MESSAGE MATCHING THIS ID";
            }

        }
        else if (request.getType().equals("FOLLOWER")){
            String[] followerRequest = request.getBody().split(" ");
//            ArrayList<String> subscriptionUsers = new ArrayList<>();
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
//            Database.showSubscriptionDatabase();
        }
        else if(request.getType().equals("REPLY")){
            String[] replyData = request.getHeader().split(" "); // ex: REPLY author:@me reply_to_id:id2END
            String msgAuthor = replyData[1].split("author:@")[1];
            String replyId = replyData[2].split("id:")[1];
            int replyIdint = Integer.parseInt(replyId);
            String msgContent = request.getBody();

            if (Database.msgExists(replyIdint)){
                Message message = new Message(msgContent,msgAuthor,Database.storageMsg.size());
                message.setReplyTo(replyIdint);
                Database.addMsg(message);

                System.out.println("replyToId = " + message.getReplyToId());
                Database.showMsgDatabase();
                answer = "OK";
            }
            else{
                if(Database.msgExists(replyIdint) || request.getBody().length()>256){
                    answer = "ERROR";
                }
            }

        }
        else if(request.getType().equals("REPUBLISH")){
            String[] dataReceived = request.getHeader().split(" "); // REPUBLISH author:@user msg_id:id END
            Integer id = Integer.parseInt(dataReceived[2].split("msg_id:")[1]);

            if(Database.msgExists(id)){
                /**
                 * new publish from id
                 */
                answer = "OK : " + Database.getMsgFromId(id).getMsg();
            }
            else{
                answer = "ERROR : NO MESSAGE MATCHING THIS ID";
            }
        }
//        else if(request.getType().equals("REPOST")){
//            String[] dataReceived = request.getHeader().split(" ");
//        }
        return answer;
    }

}
