package src.Interface;

public class ParametresInitPartie {
    private int nbJoueurs;
    private String gameMode;
    private boolean bugMode;

    public String getGameMode() {
        return gameMode;
    }

    public ParametresInitPartie(int _monInt, String _maString, boolean bugMode) {
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
