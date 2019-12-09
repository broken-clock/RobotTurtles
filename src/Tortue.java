package src;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import static src.GameLogic.plateau;

public class Tortue {
    public static String[] orientations_ = {"up", "left", "down", "right"};
    public static List orientations = Arrays.asList(orientations_);

    public class Deck {
        int cartesBleues = 18;
        int cartesJaunes = 8;
        int cartesViolettes = 8;
        int lasers = 3;
    }
    public class Position {
        int x;
        int y;
    }

    String orientation = "up";  // up | left | right | down
    int mursDePierre = 3;
    int mursDeGlace = 2;
    Deck deck = new Deck();
    Position position = new Position();
    ArrayDeque<String> prgm = new ArrayDeque<>();

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void placerMur(String typeMur, int[] coords) {
        // TODO: Vérifier si le placement de mur demandé est valide:
        //  La tortue dispose-t-elle d'un tel mur ?
        //  Le placement est-il autorisé ?

        // Placer le mur
        switch (typeMur) {
            case "P":  // Mur de pierre
                plateau.setCase(coords[0], coords[1], 'p');
                this.mursDePierre --;
                break;

            case "G":  // Mur de glace
                plateau.setCase(coords[0], coords[1], 'g');
                this.mursDeGlace --;
                break;
        }
    }

    public void completerPrgm(ArrayDeque<String> cartes) {
        // TODO vérifier la validité
        String carte;
        while (!cartes.isEmpty()) {
            carte = cartes.remove();
            this.prgm.push(carte);
            switch (carte) {
                case "B":
                    this.deck.cartesBleues --;
                    break;
                case "J":
                    this.deck.cartesJaunes --;
                    break;
                case "V":
                    this.deck.cartesViolettes --;
                    break;
                case "L":
                    this.deck.lasers --;
                    break;
            }
        }
    }

    public void executerPrgm() {
        String carte;
        while (!this.prgm.isEmpty()) {
            carte = prgm.pop();
            switch (carte) {
                    case "B":
                    this.avancer();
                    break;
                case "J":
                    this.tournerAntiHoraire();
                    break;
                case "V":
                    this.tournerHoraire();
                    break;
                case "L":
                    this.lancerLaser();
                    break;
            }
        }

    }

    // Actions élémentaires des tortues
    public void avancer() {
        Plateau.UneCase caseDestination = plateau.getCaseSuivante(this);

        switch (caseDestination.contenu) {
            // Si c'est un mur
            case 'b':
            case 'p':
            case 'g':
                // Faire demi-tour
                this.tournerHoraire();
                this.tournerHoraire();
                break;

            case 't':
                // TODO
                break;
            case Character.MIN_VALUE:  // Si la case est vide
                this.setPosition(caseDestination.x, caseDestination.y);
        }

    }

    private void tournerAntiHoraire() {
        int index = orientations.indexOf(this.orientation);
        index ++;
        if (index == orientations.size()) {
            index = 0;
        }
        this.orientation = (String) orientations.get(index);
    }

    private void tournerHoraire() {
        int index = orientations.indexOf(this.orientation);
        index --;
        if (index == -1) {
            index = orientations.size() - 1;
        }
        this.orientation = (String) orientations.get(index);
    }

    private void lancerLaser() {

    }


}
