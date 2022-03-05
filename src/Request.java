import java.util.ArrayList;
import java.util.Scanner;

public class Request {


    private String type; // PUBLISH
    private String user; // @user
    private String header; // PUBLISH author:@user
    private String body;

    private String optionUser = "";
    private String optionTag = "";
    private String optionId = "";
    private String optionLimit = "";

    private ArrayList<String> data = new ArrayList<>();


    public Request() {
        /**
         * ask for info to create request
         */
        String type = askInfo("TYPE"); // for example: PUBLISH

        if (type.equals("PUBLISH")){
            user = askInfo("USER");
            header = type.toUpperCase()+" "+"author:@"+user;
            body = askInfo("BODY")+"END";

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
            header = header + "END";
            data.add(header);
            System.out.println(">>>" + header);
        }
        else if (type.equals("RCV_MSG")){
            optionId = askInfo("id");
            header = "RCV_MSG " + "since_id:" + optionId + "END";
            System.out.println(">>>" + header);
        }
    }





    public static String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

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



}
