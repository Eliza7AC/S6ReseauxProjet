public class Requete {

    String entete;
    String corps;
    String reponse;
    String user;

    public Requete(String entete, String corps, String reponse) {
        this.entete = entete;
        this.corps = corps;
        this.reponse = reponse;
    }

    public Requete(String msg){
        this.corps = msg;
    }


    public String getCorps(){
        return this.corps;
    }
}
