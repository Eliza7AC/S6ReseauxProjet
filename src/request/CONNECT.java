package request;

import ClientSide.Client;

import java.util.Scanner;

public class CONNECT extends RequestClient{

    /** Se connecter
     *
     * — entête : CONNECT user:@user
     * — corps : aucun
     * — réponse : OK ou ERROR.
     *
     * ouverture d’une connexion avec le serveur sur laquelle sera transmis le flux de messages
     * auquel est abonné le client
     * la gestion des abonnements se fait au moyen des requêtes SUBSCRIBE/UNSUBSCRIBE.
     */
    private String user;
    private String header;

    public CONNECT() {
        user = askInfo("user");
        header = "CONNECT" + " " + "user:@" + user + " " + RequestClient.END;
        this.getInfo().add(header);
    }


    private String askInfo(String info){
        System.out.print(info + " > ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }
}
