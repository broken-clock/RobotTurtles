package src;

import src.Tuiles.Joyau;
import src.Tuiles.Position;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Plateau {
    private int taillePlateau = 8;
    String[][] plateau;

    public Plateau() {
        plateau = new String[taillePlateau][taillePlateau];
    }

    public void initPlateau(LogiqueDeJeu logiqueDeJeu) {
        // Remplir la dernière colonne de caisses en bois si besoin
        if (logiqueDeJeu.nombreJoueurs != 4) {
            for (int i = 0; i < taillePlateau; i++) {
                this.setCase(i, taillePlateau - 1, "b");
            }
        }

        // Placer les tortues
        for (Joueur joueur : logiqueDeJeu.joueurs) {
            Position position_tortue = joueur.tortue.getPosition();
            String reprTortue = joueur.tortue.getReprTortue(joueur.tortue, position_tortue.orientation);
            this.setCase(position_tortue.x, position_tortue.y, reprTortue);
        }

        // Placer les joyaux
        for (Joyau joyau : logiqueDeJeu.joyaux) {
            Position position_joyau = joyau.getPosition();
            this.setCase(position_joyau.x, position_joyau.y, "J");
        }
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }

    public String getCase(int x, int y) {
        return plateau[x][y];
    }

    public void setCase(int x, int y, String contenu) {
        plateau[x][y] = contenu;
    }

    public boolean placementBloquant(int[] coordonnees) {
        return false;
    }
}
