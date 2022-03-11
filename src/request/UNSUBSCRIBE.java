package request;

public class UNSUBSCRIBE extends RequestClient{

    /** Se désabonner
     *
     * — entête : UNSUBSCRIBE author:@user et UNSUBSCRIBE tag:#tag
     * — corps : aucun
     * — réponse : OKou ERROR. Le serveur renvoie ERROR si le client n’est pas abonné à @user ou au mot-clé
     * #tag.
     *
     * Suite à cette requête, le serveur cesse d’envoyer des messages
     * dont l’auteur est @user ou qui contiennent le  mot-clé #tag.
     */
    private String user;
    private String tag;
    private String header;

    public UNSUBSCRIBE() {
        user = askClient("user");
        tag = askClient("tag");

        header = "UNSUBSCRIBE" +  " " + "author:@" + user + "tag:#" + tag + " " + END;
        getInfo().add(header);
    }

}
