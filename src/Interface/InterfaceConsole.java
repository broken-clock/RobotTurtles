package src.Interface;

import src.LogiqueDeJeu;

public class InterfaceConsole implements Interface {
    @Override
    public void afficherPlateau(LogiqueDeJeu logiqueDeJeu) {
        int taillePlateau = logiqueDeJeu.plateau.getTaillePlateau();
        for (int i = 0; i < taillePlateau; i++) {
            for (int j = 0; j < taillePlateau; j++) {
                System.out.print(logiqueDeJeu.plateau.getCase(i, j) != Character.MIN_VALUE ? logiqueDeJeu.plateau.getCase(i, j) : '.');
                System.out.print("\t");
            }
            System.out.println("");
        }
    }
}
