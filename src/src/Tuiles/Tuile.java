package src.Tuiles;

import src.Case;
import src.LogiqueDeJeu;

public class Tuile {
    protected Position position = new Position(0, 0, Orientations.UP);

    public Position getPosition() {
        return this.position;

    }

    public void setPosition(int x, int y, Orientations orientation) {
        this.position.x = x;
        this.position.y = y;
        this.position.orientation = orientation;
    }

    public Case getCaseSuivante(LogiqueDeJeu logiqueDeJeu) {
        int x = this.position.x;
        int y = this.position.y;

        switch (this.position.orientation) {
            case UP:
                x--;
                break;
            case DOWN:
                x++;
                break;
            case LEFT:
                y--;
                break;
            case RIGHT:
                y++;
                break;
        }

        if (x < 0 || y < 0 || x > logiqueDeJeu.plateau.getTaillePlateau() - 1 || y > logiqueDeJeu.plateau.getTaillePlateau() - 1) {
            // On suppose que le contour du plateau est formé de caisses en bois
            return new Case(-1, -1, "b");  // -1 pour signifier qu'on est en dehors du plateau
        }

        return new Case(x, y, logiqueDeJeu.plateau.getCase(x, y));
    }
}
