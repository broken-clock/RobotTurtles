package src;

import src.Interface.Affichage;

public class Main {
    public static void main(String[] args) {

        LogiqueDeJeu logiqueDeJeu = new LogiqueDeJeu();

        logiqueDeJeu.initialiserPartie();
        Affichage test = new Affichage();

        logiqueDeJeu.lancerPartie();

    }
}
