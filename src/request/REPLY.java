package request;

public class REPLY extends RequestClient {
    /** Publier un message en réponse à un autre
     *
     * — entête : REPLY author:@user reply_to_id:id
     * — corps : contenu du message
     * — réponse :OK ou ERROR
     */
    private String user;
    private String id;
    private String body;
    private String header;

    public REPLY() {
        user = this.askClient("user");
        id = this.askClient("id");
        header = "REPLY" + " " + "author:@" + user + " " + "reply_to_id:" + id;
        body = this.askClient("body");
        body = body + " " + END;
        this.getInfo().add(header);
        this.getInfo().add(body);
    }
}
