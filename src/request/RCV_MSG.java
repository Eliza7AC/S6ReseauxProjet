package request;

public class RCV_MSG extends RequestClient {

    /** Recevoir un message
     *
     * — entête : RCV_MSG msg_id:id
     * — corps : aucun
     * — réponse : réponse MSG contenant le msg dont l’id est id
     * ou ERROR si il n’existe pas de message dont l’id est id
     */
    private String id;
    private String header;

    public RCV_MSG() {
        id = this.askClient("id");
        header = "RCV_MSG" + " " + "msg_id:" + id;
        header = header + " " + END;
        this.getInfo().add(header);
    }


    // sending RCV_MSG from id contained in String
    public RCV_MSG(String idString) {
        id = idString;
        header = "RCV_MSG" + " " + "msg_id:" + id;
        header = header + " " + END;
        this.getInfo().add(header);
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


}
