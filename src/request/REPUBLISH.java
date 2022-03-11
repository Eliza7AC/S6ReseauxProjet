package request;

public class REPUBLISH extends RequestClient {

    /** Re-publier un message
     *
     * — entête : REPUBLISH author:@user msg_id:id
     * — corps : aucun
     * — réponse :OK ou ERROR
     *  Le message dont l’identifiant est id est publié de nouveau.
     */
    private String user;
    private String id;
    private String header;

    public REPUBLISH() {
        user = askClient("user");
        id = askClient("id");
        header = "REPUBLISH" + " " + "author:@" + user + " " + "msg_id:" + id + " " + END;
        this.getInfo().add(header);
    }

    public REPUBLISH(String user, String id) {
        this.user = user;
        this.id = id;
        header = "REPUBLISH" + " " + "author:@" + user + " " + "msg_id:" + id + " " + END;
        this.getInfo().add(header);
    }
}
