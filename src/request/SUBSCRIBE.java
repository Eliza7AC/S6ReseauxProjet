package request;

public class SUBSCRIBE extends RequestClient{

    /** S’abonner
     *
     * — entête : SUBSCRIBE author:@user et SUBSCRIBE tag:#tag
     * — corps : aucun
     * — réponse : OK ou ERROR. Le serveur envoie ERROR si @user n’est pas géré par l’application. Si par contre
     *
     * le mot-clé #tag n’est pas encore géré, il est ajouté et OK est renvoyé.
     * Suite à cette requête, le serveur enverra une réponse MSG sur la connexion
     * précédemment ouverte par CONNECT
     * à chaque fois qu’un message dont l’auteur est @user ou qui contient le mot-clé #tag est publié.
     */
    private String user;
    private String tag;
    private String header;

    public SUBSCRIBE() {
        user = askClient("user");
        tag = askClient("tag");

        header = "SUBSCRIBE" +  " " + "author:@" + user + "tag:#" + tag + " " + END;
        getInfo().add(header);
    }
}
