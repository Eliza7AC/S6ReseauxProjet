package request;

public class RCV_IDS extends RequestClient {

    /** Recevoir des identifiants de messages
     *
     * — entête : RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n]
     * — corps : aucun
     * — réponse : réponse MSG_IDS contenant les id des n messages les plus récents
     *   ordre antichronologiques des id (les plus récents en premier)
     *
     * — (optionnel) author:@user l’auteur des messages est @user
     * — (optionnel) tag:#tag les messages contiennent le mot clé #tag
     * — (optionnel) since_id:id les messages ont été publiés après le message dont l’identifiant est id
     * — (optionnel) limit:n aux plus n identifiants sont renvoyés. La valeur par défaut de n est n=5.
     */
    private String user;
    private String tag;
    private String id;
    private String limit;
    private String header;

    public RCV_IDS() {
        user = this.askClient("(option) user");
        tag = this.askClient("(option) tag");
        id = this.askClient("(option) id");
        limit = this.askClient("(option) limit");

        header = constructHeader();
        getInfo().add(header);
    }


    private String constructHeader(){
        String h = "RCV_IDS" + " ";

        if (!user.isEmpty()){
            h = h + "author:@"+user + " ";
        }
        if (!tag.isEmpty()){
            h = h + "tag:#" + tag + " ";
        }
        if (!id.isEmpty()){
            h = h + "since_id:" + id + " ";
        }
        if (!limit.isEmpty()){
            h = h + "limit:" + limit;
        }
        h = h + " " + END;
        return h;
    }


}
