import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Request {

    /** Request Class is used by both server and client sides
     *
     * two kinds of request can be created:
     * the first one = client side (infos are asked to the client)
     * the second one = server side (infos retrieved by server when data received)
     */
    public static List<String> headers = Arrays.asList("PUBLISH", "RCV_IDS", "RCV_MSG", "REPLY", "REPUBLISH", "FOLLOWER");

    private String type; // PUBLISH
    private String user; // user
    private String header; // PUBLISH author:@user
    private String body; // msg

    private String optionUser = "";
    private String optionTag = "";
    private String optionId = "";
    private String optionLimit = "";

    public static final String END = " END";

    // to iterate through when sending info from client to server
    private ArrayList<String> data = new ArrayList<>();



    /****************************
     * ****** CLIENT SIDE *******
     * **************************/

    /**
     * request sent, client side
     * with infos asked to client
     * THE REQUEST MUST ENDED WITH "END" WORD
     */
    public Request() {
        String type = askInfo("TYPE"); // for example: PUBLISH

        if (type.equals("PUBLISH")){
            user = askInfo("USER");
            header = type.toUpperCase()+" "+"author:@"+user;
            body = askInfo("BODY")+END;

            data.add(header);
            data.add(body);
        }
        else if (type.equals("RCV_IDS")){
            optionUser = askInfo("(option) user");
            optionTag = askInfo("(option) tag");
            optionId = askInfo("(option) id");
            optionLimit = askInfo("(option) limit");

            header = type.toUpperCase()+" ";

            if (!optionUser.isEmpty()){
                header = header + "author:@"+optionUser + " ";
            }
            if (!optionTag.isEmpty()){
                header = header + "tag:#" + optionTag + " ";
            }
            if (!optionId.isEmpty()){
                header = header + "since_id:" + optionId + " ";
            }
            if (!optionLimit.isEmpty()){
                header = header + "limit:" + optionLimit + " ";
            }
            header = header + END;
            data.add(header);
//            System.out.println(">>>" + header);
        }
        else if (type.equals("RCV_MSG")){
            optionId = askInfo("id");
            header = "RCV_MSG " + "since_id:" + optionId + END;
            data.add(header);
//            System.out.println(">>>" + header);
        }
//        else if (type.equals("FOLLOWER")){
//            System.out.println("ceci est un follower");
//            header = "FOLLOWER " + "END";
//            data.add(header);
//        }
        else if (type.equals("REPLY")){
            user = askInfo("author");
            optionId = askInfo("id");
            header = "REPLY " + "author:@" + user + " reply_to_id:"+optionId;
            body = askInfo("BODY")+END;
            data.add(header);
            data.add(body);
        }
        else if (type.equals("REPUBLISH")){
            optionUser = askInfo("user");
            optionId = askInfo("id");
            header = "REPUBLISH author:@"+optionUser+" msg_id:"+optionId + END;
            data.add(header);
        }
    }

    public static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }



    /****************************
     * ****** SERVER SIDE *******
     * **************************/

    /**
     * request received, server side
     * = creation of request from header received by server
     * + update of request when body is received by server
     */
    public Request(String header){
        this.header = header;

        String[] infoReceivedArr = header.split(" ");
        String firstWord = infoReceivedArr[0];


        if(isHeader(firstWord)){
            type = firstWord;

            /**
             * identifying author
             */
            if(requestContains("author:@", infoReceivedArr)){
                user = getInfoFromRequest("@", infoReceivedArr); // author:@aline
                String[] userString = user.split("@"); // author: aline
                user = userString[1]; // aline
                System.out.println("user ==== " + user);
            }
            /**
             * identifying tag
             */
            if(requestContains("tag:#", infoReceivedArr)){
                optionTag = getInfoFromRequest("#", infoReceivedArr); // tag:#bonjour
                String[] tagString = optionTag.split("#"); // tag: bonjour
                optionTag = tagString[1]; // bonjour
                System.out.println("tag === " + optionTag);
            }
            /**
             * identifying id
             */
//                            System.out.println(requestContains("since_id:",infoReceivedArr));
            if(requestContains("since_id:",infoReceivedArr)){
                optionId = getInfoFromRequest("since_id", infoReceivedArr); // since_id:5
                String[] idString = optionId.split("since_id:"); // 5
                optionId = idString[1];
                System.out.println("id === " + optionId);
            }
            if(requestContains("msg_id:",infoReceivedArr)){
                optionId = getInfoFromRequest("msg_id", infoReceivedArr); // msg_id:5
                String[] idString = optionId.split("msg_id:"); // 5
                optionId = idString[1];
                System.out.println("id === " + optionId);
            }

            /**
             * identifying limit
             */
            if(requestContains("limit:",infoReceivedArr)){
                optionLimit = getInfoFromRequest("limit:", infoReceivedArr); // limit:10
                String[] limitString = optionLimit.split("limit:"); // 10
                optionLimit = limitString[1];
                System.out.println("limit === " + optionLimit);
            }

        }

    }

    public void update(String body){
        System.out.println("----------- " + body);
        String properBody = body.substring(0,body.length()-3);
        System.out.println("----------- " + properBody);
        this.body = properBody;
    }

    public static boolean requestContains(String pattern, String[] requestReceived){
        for(String s: requestReceived){
            if (s.contains(pattern)){
                return true;
            }
        }
        return false;
    }

    public static String getInfoFromRequest(String pattern, String[] requestReceived){
        for(String s: requestReceived){
            if (s.contains(pattern)){
                return s;
            }
        }
        return null;
    }





    /****************************
     * ******* BOTH SIDES *******
     * **************************/


    public String getType() {
        return type;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public String toString(){
        return "### " + header + "\n"
                + "### " + body;
    }

    private static void log(String str) {
        System.out.println(str);
    }

    public static boolean isHeader(String s){
        for(String header: headers){
            if (s.equals(header)){
                return true;
            }
        }
        return false;
    }

    public static List<String> getHeaders() {
        return headers;
    }

    public String getUser() {
        return user;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String getOptionUser() {
        return optionUser;
    }

    public String getOptionTag() {
        return optionTag;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getOptionLimit() {
        return optionLimit;
    }

}
