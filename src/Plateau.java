package src;

import src.Tuiles.Joyau;
import src.Tuiles.Position;

import java.sql.SQLOutput;

public class Plateau {
    private int taillePlateau = 8;
    char[][] plateau;

    public Plateau() {
        plateau = new char[taillePlateau][taillePlateau];
    }

    public void initPlateau(LogiqueDeJeu logiqueDeJeu) {
        // Remplir la dernière colonne de caisses en bois si besoin
        if (logiqueDeJeu.nombreJoueurs != 4) {
            for (int i = 0; i < taillePlateau; i++) {
                this.setCase(i, taillePlateau - 1, 'b');
            }
        }

        // Placer les tortues
        for (Joueur joueur : logiqueDeJeu.joueurs) {
            Position position_tortue = joueur.tortue.getPosition();
            System.out.println(position_tortue);
//            this.setCase(position_tortue.x, position_tortue.y, 'T');
        }

        // Placer les joyaux
        for (Joyau joyau : logiqueDeJeu.joyaux) {
            Position position_joyau = joyau.getPosition();
//            this.setCase(position_joyau.x, position_joyau.y, 'J');
        }
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }

    public char getCase(int x, int y) {
        return plateau[x][y];
    }

    public void setCase(int x, int y, char contenu) {
        plateau[x][y] = contenu;
    }

//    public Case getCaseSuivante(Joueur tortue) {
//        int x = tortue.position.x;
//        int y = tortue.position.y;
//
//        switch (tortue.orientation) {
//            case "up":
//                x--;
//                break;
//            case "down":
//                x++;
//                break;
//            case "left":
//                y--;
//                break;
//            case "right":
//                y++;
//                break;
//        }
//
//        if (x < 0 || y < 0) {
//            // On suppose que le contour du plateau est formé de caisses en bois
//            return new Case(-1, -1, 'b');  // -1 pour signifier qu'on est en dehors du plateau
//        }
//
//        return new Case(x, y, this.getCase(x, y));
//    }
}
