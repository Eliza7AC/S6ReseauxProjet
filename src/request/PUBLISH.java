package request;

public class PUBLISH extends RequestClient {

    /** Publier un message
     *
     * — entête : PUBLISH author:@user
     * — corps : contenu du message
     * — réponse :OK ou ERROR
     */
    private String user;
    private String header;
    private String body;

    public PUBLISH() {
        user = askClient("user");
        header = "PUBLISH author:@"+user;

        body = askClient("body");
        body = body + " " + END;

        this.getInfo().add(header);
        this.getInfo().add(body);

    }



}
