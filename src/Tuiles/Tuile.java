package src.Tuiles;

import src.Case;
import src.LogiqueDeJeu;

public class Tuile {
    Position position = new Position(0, 0, Orientations.UP);

    public Position getPosition() {
        return this.position;

    }

    public void setPosition(int x, int y, Orientations orientation) {
        this.position.setX(x);
        this.position.setY(y);
        this.position.setOrientation(orientation);
    }

    Case getCaseSuivante(LogiqueDeJeu logiqueDeJeu) {
        int x = this.position.getX();
        int y = this.position.getY();

        switch (this.position.getOrientation()) {
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

        // Si une tortue rencontre un bord de plateau
        if (x < 0 || y < 0 || x > logiqueDeJeu.getPlateau().getTaillePlateau() - 1 || y > logiqueDeJeu.getPlateau().getTaillePlateau() - 1) {
            return new Case(-1, -1, "bord");  // x et y n'importent pas
        }

        return new Case(x, y, logiqueDeJeu.getPlateau().getCase(x, y));
    }
}
