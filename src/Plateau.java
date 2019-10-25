package src;

import static src.GameLogic.tortues;

public class Plateau {
    public class UneCase {
        int x;
        int y;
        char contenu;
        public UneCase(int x_, int y_, char contenu_) {
            x = x_;
            y = y_;
            contenu = contenu_;
        }
    }

    public static int taillePlateau = 8;
    char[][] plateau = new char[taillePlateau][taillePlateau];

    public char getCase(int x, int y) {
        return plateau[x][y];
    }

    public void setCase(int x, int y, char contenu) {
        plateau[x][y] = contenu;
    }

    public void initPlateau(int nombreTortues) {
        // Remplir la dernière colonne de caisses en bois si besoin
        if (nombreTortues != 4) {
            for (int i=0; i<taillePlateau; i++) {
                this.setCase(i, taillePlateau - 1, 'b');
            }
        }

        // Placer les joyaux
        switch (nombreTortues) {
            case 2:
                this.setCase(taillePlateau - 1, 3, 'j');
                break;

            case 3:
                this.setCase(taillePlateau - 1, 0, 'j');
                this.setCase(taillePlateau - 1, 3, 'j');
                this.setCase(taillePlateau - 1, 6, 'j');
                break;

            case 4:
                this.setCase(taillePlateau - 1, 1, 'j');
                this.setCase(taillePlateau - 1, 6, 'j');
                break;
        }

        // Placer les tortues
        // TODO différencier les tortues dans le plateau
        for (int i = 0; i< nombreTortues; i++) {
            this.setCase(tortues[i].position.x, tortues[i].position.y, 't');
        }
    }

    public void afficherPlateau() {
        for (int i=0; i<taillePlateau; i++) {
            for (int j=0; j<taillePlateau; j++) {
                System.out.print(this.getCase(i, j) != Character.MIN_VALUE ? this.getCase(i, j) : '.');
                System.out.print("\t");
            }
            System.out.println("");
        }
    }

    public UneCase getCaseSuivante(Tortue tortue) {
        int x = tortue.position.x;
        int y = tortue.position.y;

        switch (tortue.orientation) {
            case "up":
                x--;
                break;
            case "down":
                x++;
                break;
            case "left":
                y--;
                break;
            case "right":
                y++;
                break;
        }

        if (x < 0 || y < 0) {
            // On suppose que le contour du plateau est formé de caisses en bois
            return new UneCase(-1, -1, 'b');  // -1 pour signifier qu'on est en dehors du plateau
        }

        return new UneCase(x, y, this.getCase(x, y));
    }
}
