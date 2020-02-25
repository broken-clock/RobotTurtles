package src;

public class Parametres {
    int nbJoueurs;
    String gameMode;
    boolean bugMode;

    public Parametres(int _monInt, String _maString,boolean bugMode) {
        this.nbJoueurs = _monInt;
        this.gameMode = _maString;
        this.bugMode = bugMode;
    }
    public int getNbJoueurs() {
    	return nbJoueurs;
    }
    public String getModeJeu() {
    	return gameMode;
    }
    public boolean getModeBug() {
    	return bugMode;
    }
}
